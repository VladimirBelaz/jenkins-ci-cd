FROM jenkins/jenkins:lts-jdk21

USER root

# Устанавливаем необходимые пакеты
RUN apt-get update && \
    apt-get install -y \
      ca-certificates \
      curl \
      gnupg \
      git \
      lsb-release \
      python3-pip \
      python3-venv \
      openjdk-21-jdk \
      libglib2.0-0 \
      libnss3 \
      libx11-6 \
      libxcb1 \
      libxkbcommon0 \
      libatspi2.0-0 \
      libxcomposite1 \
      libxdamage1 \
      libxext6 \
      libxfixes3 \
      libxrandr2 \
      libgbm1 \
      libcairo2 \
      libpango-1.0-0 \
      libasound2 \
      libxshmfence1 \
      && apt-get clean

# Устанавливаем Jenkins Job Builder
RUN python3 -m venv /opt/jjb-venv && \
    /opt/jjb-venv/bin/pip install --upgrade pip wheel "setuptools<82" && \
    /opt/jjb-venv/bin/pip install jenkins-job-builder && \
    ln -sf /opt/jjb-venv/bin/jenkins-jobs /usr/local/bin/jenkins-jobs

# Устанавливаем Docker CLI и добавляем пользователя jenkins в группу docker
RUN install -m 0755 -d /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc && \
    chmod a+r /etc/apt/keyrings/docker.asc && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian $(. /etc/os-release && echo $VERSION_CODENAME) stable" > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce-cli docker-compose-plugin && \
    usermod -aG docker jenkins

# Устанавливаем kubectl (для работы с Kubernetes)
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    chmod +x ./kubectl && \
    mv ./kubectl /usr/local/bin/

# Переключаемся обратно на пользователя jenkins
USER jenkins

# Устанавливаем плагины Jenkins (по желанию)
RUN jenkins-plugin-cli --plugins \
    "allure-jenkins-plugin" \
    "docker-plugin" \
    "kubernetes" \
    "pipeline-aws" \
    "git" \
    "workflow-aggregator" \
    "credentials-binding" \
    "job-dsl"