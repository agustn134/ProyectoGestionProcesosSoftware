FROM ghcr.io/google/osv-scanner:v2.7.0 AS osv
FROM ghcr.io/gitleaks/gitleaks:v8.30.1 AS gitleaks
FROM jenkins/jenkins:2.541.3-jdk21
USER root
RUN apt-get update && apt-get install -y --no-install-recommends python3 \
    && rm -rf /var/lib/apt/lists/*
COPY --from=osv /osv-scanner /usr/local/bin/osv-scanner
COPY --from=gitleaks /usr/bin/gitleaks /usr/local/bin/gitleaks
USER jenkins
COPY --chown=jenkins:jenkins java21-tools.groovy /usr/share/jenkins/ref/init.groovy.d/java21-tools.groovy
