#!/usr/bin/env python3
"""Verify the server processed this exact CI revision; do not equate upload with approval."""
import argparse
import json
import os
import re
import subprocess
import time
import urllib.parse
import urllib.request
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--module', required=True)
parser.add_argument('--project', required=True)
args = parser.parse_args()
server = os.environ['SONAR_HOST_URL'].rstrip('/')
token = os.environ['SONAR_TOKEN']
task_file = Path(args.module)/'target/sonar/report-task.txt'
properties = dict(line.split('=',1) for line in task_file.read_text().splitlines() if '=' in line)
task_url = properties['ceTaskUrl']
assert urllib.parse.urlsplit(task_url).netloc == urllib.parse.urlsplit(server).netloc, 'Servidor inesperado en report-task'

def get(path):
    url = path if path.startswith(server+'/') else server+path
    request = urllib.request.Request(url, headers={'Authorization': 'Bearer '+token})
    with urllib.request.urlopen(request, timeout=30) as response:
        return json.load(response)

task = None
for attempt in range(120):
    task = get(task_url)['task']
    if task['status'] not in ('PENDING','IN_PROGRESS'):
        break
    time.sleep(3)
if task['status'] != 'SUCCESS':
    raise SystemExit('Sonar no procesó el análisis: '+task['status'])
analysis_id = task['analysisId']
gate = get('/api/qualitygates/project_status?analysisId='+urllib.parse.quote(analysis_id))
measures = get('/api/measures/component?component='+args.project+'&metricKeys=coverage,bugs,vulnerabilities,code_smells,security_hotspots,ncloc')
assert task['id'] == properties['ceTaskId'] and task['componentKey'] == args.project, 'La tarea no pertenece al reporte de este build'
revision = subprocess.check_output(['git','rev-parse','HEAD'],text=True).strip()
subprocess.run(['git','diff','--exit-code','--',args.module],check=True)
scanner_log = (Path('ci-reports')/args.module/'sonar-scanner.log').read_text(encoding='utf-8')
scm = re.findall(r"SCM revision ID '([0-9a-f]{40})'",scanner_log)
assert scm and scm[-1] == revision, 'El SCM reportado por el escáner no coincide con el checkout'
metrics = {entry['metric']: entry.get('value') for entry in measures['component']['measures']}
assert 'vulnerabilities' in metrics and str(metrics['vulnerabilities']).isdecimal(), 'Falta una métrica válida de vulnerabilidades'
assert 'ncloc' in metrics and str(metrics['ncloc']).isdecimal() and int(metrics['ncloc']) > 0, 'No se acreditó código fuente analizado'
passed = gate['projectStatus']['status'] == 'OK' and int(metrics['vulnerabilities']) == 0
result = {'projectKey': args.project, 'analysisId': analysis_id, 'revision': revision,
          'revisionEvidence': 'SCM del escáner + checkout limpio + ceTaskId del reporte procesado',
          'processed': task['status'], 'qualityGate': gate, 'metrics': metrics,
          'policy': 'Quality Gate OK y cero vulnerabilidades reportadas por Sonar; no demuestra ausencia de todo riesgo',
          'status': 'PASS' if passed else 'FAIL'}
output = Path('ci-reports')/args.module/'sonar-summary.json'
output.parent.mkdir(parents=True,exist_ok=True)
output.write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print('Sonar: procesado='+task['status']+', gate='+gate['projectStatus']['status']+', vulnerabilidades='+metrics.get('vulnerabilities','N/D'))
raise SystemExit(0 if passed else 1)
