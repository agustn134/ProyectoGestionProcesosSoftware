#!/usr/bin/env python3
"""Fail on OSV findings or an unusable inventory; retain full, unfiltered evidence."""
import argparse
import json
import re
from datetime import date
import xml.etree.ElementTree as ET
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--report', required=True)
parser.add_argument('--output', required=True)
parser.add_argument('--module')
parser.add_argument('--context-policy')
args = parser.parse_args()
data = json.loads(Path(args.report).read_text(encoding='utf-8-sig'))
packages = {}
findings = {}
for result in data.get('results', []):
    for entry in result.get('packages', []):
        package = entry.get('package', {})
        name, version = package.get('name'), package.get('version')
        if name and version:
            packages[(name, version)] = package
        for vulnerability in entry.get('vulnerabilities', []):
            identity = (name, version, vulnerability['id'])
            findings[identity] = {'package': name, 'version': version, 'id': vulnerability['id'],
                                  'aliases': vulnerability.get('aliases', []),
                                  'summary': vulnerability.get('summary', ''),
                                  'severityLabel': vulnerability.get('database_specific', {}).get('severity', 'NOT_PROVIDED'),
                                  'cwe': vulnerability.get('database_specific', {}).get('cwe_ids', []),
                                  'severity': vulnerability.get('severity', []),
                                  'references': vulnerability.get('references', [])}
if not packages:
    raise SystemExit('ERROR: inventario vacío; no puede declararse un escaneo limpio.')
reviewed = []
if args.context_policy and args.module and findings:
    policy = json.loads(Path(args.context_policy).read_text(encoding='utf-8'))
    eligible = [f for f in findings.values() if any(f['id'] == r['id'] and f['package'] == r['package'] and f['version'] == r['version'] for r in policy['rules'])]
    if eligible:
        assert date.today() <= date.fromisoformat(policy['expires']), 'Revisión contextual vencida: debe reevaluarse'
        source = Path(args.module)/'src/main'
        assert source.is_dir() and any(source.rglob('*.java')), 'No hay fuente Java que permita comprobar el contexto'
        forbidden = re.compile(r'''SseEmitter|ServerSentEvent|text/event-stream|XsltView|["'][^"']*\*\*[^"']*["']''', re.I)
        for path in source.rglob('*'):
            if path.is_file() and path.suffix in ('.java','.xml','.properties','.yaml','.yml'):
                assert not forbidden.search(path.read_text(encoding='utf-8-sig')), 'Se introdujeron condiciones que invalidan la excepción: '+str(path)
        checks = []
        for report in (Path(args.module)/'target/surefire-reports').glob('TEST-*.xml'):
            for test in ET.parse(report).getroot().iter('testcase'):
                if test.get('name','') == policy['requiredPassingTest']:
                    checks.append(all(test.find(tag) is None for tag in ('failure','error','skipped')))
        assert checks and all(checks), 'Falta una prueba MVC real y aprobada de las condiciones de aplicabilidad'
        for finding in eligible:
            rule = next(r for r in policy['rules'] if r['id'] == finding['id'])
            finding['disposition'] = 'VERSION_AFFECTED_CONDITIONS_NOT_OBSERVED'
            finding['contextualReview'] = rule
            reviewed.append(finding)
blocking = len(findings)-len(reviewed)
status = 'FAIL' if blocking else ('PASS_WITH_CONTEXTUAL_EXCEPTIONS' if reviewed else 'PASS')
result = {'tool': 'OSV-Scanner', 'version': '2.7.0', 'inventory': 'CycloneDX generado por Maven; incluye scope test',
          'packageCount': len(packages), 'findingCount': len(findings),
          'status': status, 'blockingFindingCount': blocking, 'reviewedContextualFindings': reviewed, 'suppressedFindings': [],
          'findings': sorted(findings.values(), key=lambda f: (f['package'], f['id']))}
Path(args.output).write_text(json.dumps(result, ensure_ascii=False, indent=2)+'\n', encoding='utf-8')
print(f"OSV: {len(packages)} componentes, {len(findings)} avisos conservados, {blocking} bloqueantes; {result['status']}")
raise SystemExit(1 if blocking else 0)
