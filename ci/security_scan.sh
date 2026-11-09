#!/bin/sh
set -eu
module="$1"
case "$module" in
  Ejercicio5Cliente|Ejercicio5MyAPIGestor|db-tests) ;;
  *) echo 'Módulo fuera del alcance permitido'; exit 2 ;;
esac
report="$WORKSPACE/ci-reports/$module"
mkdir -p "$report"
python3 --version > "$report/tool-versions.txt"
osv-scanner --version >> "$report/tool-versions.txt"
gitleaks version >> "$report/tool-versions.txt"
python3 -m unittest discover -s ci/tests
python3 ci/scan_source.py --paths "$module" --output "$report/source-checks.json"
mvn -B -ntp -f "$module/pom.xml" org.cyclonedx:cyclonedx-maven-plugin:2.9.1:makeBom \
  -DincludeTestScope=true -DoutputFormat=json -DoutputName=bom "-DoutputDirectory=$report"
set +e
osv-scanner scan source -L "$report/bom.json" --all-packages --format=json > "$report/osv.json"
status=$?
set -e
case "$status" in
  0|1) ;;
  *) echo "OSV no completó el análisis: código $status"; exit "$status" ;;
esac
# Gitleaks revisa código actual, no el historial. No se ocultan hallazgos ni valores de salida.
gitleaks dir "$module" --config .gitleaks.toml --redact=100 \
  --report-format=json --report-path="$report/gitleaks.json" --exit-code=1
python3 ci/security_gate.py --report "$report/osv.json" --output "$report/security-summary.json" \
  --module "$module" --context-policy ci/contextual-advisories.json
