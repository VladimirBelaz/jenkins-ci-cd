# Jenkins CI/CD для UI и Mobile автотестов

Данный проект представляет собой полностью готовую инфраструктуру для запуска автотестов на Jenkins с использованием Jenkins Job Builder, Allure и Docker.

## Что реализовано

- Jenkins запускается через **Docker Compose**.
- Все конфигурации джоб хранятся в виде **YAML-кода** (Infrastructure as Code).
- **Jenkins Job Builder** автоматически создаёт и обновляет джобы на Jenkins.
- Реализованы джобы для запуска **UI (Selenium)** и **Mobile (Appium)** тестов.
- Настроена генерация **Allure-отчётов**.
- Реализована общая джоба `running_tests` для параллельного запуска всех тестов.

## Технологии

- **Jenkins** — CI/CD сервер.
- **Jenkins Job Builder** — управление конфигурациями через YAML.
- **Selenium WebDriver** — UI-тесты.
- **Appium** — мобильные тесты.
- **Allure** — генерация отчётов.
- **Docker** — изоляция и воспроизводимость окружения.
- **Maven** — сборка Java-проектов.
- **Chrome** — браузер для UI-тестов.

## Запуск Jenkins

### 1. Клонируйте репозиторий

```bash
git clone https://github.com/VladimirBelaz/jenkins-ci-cd.git
cd jenkins-ci-cd
```
### 2. Запустите Jenkins через Docker Compose
```bash
docker-compose up -d
```

### 3. Получите пароль для входа
   bash
```bash
docker exec -it jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```
### 4. Откройте Jenkins в браузере
   text

http://localhost:8080

## Настройка Jenkins
### 1. Добавьте credentials

После первого входа добавьте два типа credentials:

    github (Username with password) — ваш логин и GitHub-токен.

    jenkins_api (Username with password) — логин и пароль от Jenkins.

Подробная инструкция: Jenkins Credentials.
### 2. Запустите джобу jobs_uploader

Она создаст все остальные джобы:

- **ui_tests**
- **mobile_tests**
- **running_tests**

## Запуск тестов
UI-тесты (ui_tests)

- **Выберите браузер (chrome, firefox, edge).**
- **Включите/отключите Headless-режим.**
- **Нажмите Build.**
- **После завершения откроется Allure-отчёт.**

## Mobile-тесты (mobile_tests)

- **Укажите APK_URL (по умолчанию используется APK из репозитория wishlist-mobile-tests).**
- **Нажмите Build.**
- **Jenkins скачает APK, поднимет эмуляторы и Appium через Docker Compose.**
- **После завершения тестов отчёт будет доступен в Allure.**

## Запуск всех тестов вместе (running_tests)

- **Выберите, какие тесты запускать (UI, Mobile или оба).**
- **Нажмите Build.**
- **Джоба запустит выбранные тесты параллельно.**

## Allure-отчёты

Allure-отчёты автоматически генерируются после каждой сборки и доступны по ссылке Allure Report на странице джобы.