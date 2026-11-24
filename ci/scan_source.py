#!/usr/bin/env python3
"""Small, explicit CI checks; complements Sonar/OSV/Gitleaks, not a general SAST."""
import argparse
import hashlib
import json
import re
from pathlib import Path

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--paths', nargs='+', required=True)
    parser.add_argument('--output', required=True)
    args = parser.parse_args()
    extensions = {'.java', '.sql', '.properties', '.yaml', '.yml'}
    excluded = {'target', '.git', '.idea', '.tools', 'practica-3', 'entrega-unidad2', 'ci-reports'}
    rules = [
        ('JAVA-NULL', 'HIGH', r'\.equals\(null\)', 'Comparación null insegura; usar una comprobación explícita.'),
        ('CONFIG-SECRET', 'HIGH', r'^\s*spring\.datasource\.password\s*=\s*\$\{[^}:]+:[^}]+\}', 'Contraseña predeterminada dentro de la configuración.'),
        ('SQL-GLOBAL', 'HIGH', r'\b(?:DROP\s+DATABASE|SET\s+GLOBAL|GRANT\s+ALL\b[^;]*\*\.\*)', 'Operación SQL global fuera del alcance del esquema de la aplicación.'),
    ]
    files, findings, omitted = [], [], set()
    for value in args.paths:
        source = Path(value)
        if not source.exists():
            raise ValueError(f'Ruta de análisis inexistente: {source}')
        for path in sorted(source.rglob('*') if source.is_dir() else [source]):
            if not path.is_file():
                continue
            if any(part in excluded for part in path.parts):
                omitted.add(next(part for part in path.parts if part in excluded))
                continue
            if path.suffix.lower() not in extensions:
                continue
            raw = path.read_bytes()
            text = raw.decode('utf-8-sig')
            files.append({'path': path.as_posix(), 'sha256': hashlib.sha256(raw).hexdigest()})
            for line_number, line in enumerate(text.splitlines(), 1):
                for rule, severity, pattern, message in rules:
                    if re.search(pattern, line, re.IGNORECASE):
                        findings.append({'rule': rule, 'severity': severity, 'path': path.as_posix(), 'line': line_number, 'message': message})
    if not files:
        raise ValueError('No se reconocieron archivos de código/configuración: el análisis no puede declararse limpio.')
    result = {'tool': 'comprobaciones-locales-ci', 'version': '1.0', 'scope': args.paths,
              'limitations': 'Tres reglas explícitas; no reemplaza SAST, SCA ni revisión manual.',
              'exclusions': sorted(excluded), 'omittedDirectoriesObserved': sorted(omitted),
              'scannedFiles': files, 'findings': findings}
    output = Path(args.output)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(result, ensure_ascii=False, indent=2)+'\n', encoding='utf-8')
    print(f'Código/configuración: {len(files)} archivos, {len(findings)} hallazgos; {output}')
    return 1 if findings else 0

if __name__ == '__main__':
    try:
        raise SystemExit(main())
    except (ValueError, OSError) as error:
        print(f'ERROR DE ANÁLISIS: {error}')
        raise SystemExit(2)
