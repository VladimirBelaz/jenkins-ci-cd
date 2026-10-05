# Jenkins CI/CD для UI, Mobile и API автотестов

Полностью готовая инфраструктура для запуска автотестов на Jenkins
с использованием Jenkins Job Builder, Allure, Selenoid и Docker.

## Что реализовано

- Jenkins запускается через Docker Compose.
- Все конфигурации джоб хранятся в виде YAML-кода (Infrastructure as Code).
- Jenkins Job Builder автоматически создаёт и обновляет джобы в Jenkins.
- Реализованы джобы для запуска UI (Selenium/Selenoid), Mobile (Appium) и API (RestAssured) тестов.
- Настроена генерация Allure-отчётов с видео прогона UI-тестов.
- Единая джоба running_tests запускает все три типа тестов параллельно.
- Запуск по push в репозиторий и по расписанию (каждый день в полночь).

## Архитектура

Проект состоит из трёх репозиториев:

- jenkins-ci-cd - инфраструктура Jenkins, YAML-конфиги джоб, JJB. Джобы: jobs_uploader, running_tests.
- otus-courses - UI и API тесты. Джобы: ui_tests (ветка main), api_tests (ветка homework_3).
- wishlist-mobile-tests - мобильные тесты на Appium. Джоба: mobile_tests.

## Технологии

- Jenkins - CI/CD сервер.
- Jenkins Job Builder - управление конфигурациями через YAML.
- Selenoid + Selenoid UI - запуск браузеров в Docker с записью видео.
- Selenium WebDriver - UI-тесты.
- Appium - мобильные тесты.
- RestAssured + WireMock + JsonSchemaValidation + Cucumber - API-тесты.
- Allure - генерация отчётов.
- Docker Compose - изоляция и воспроизводимость окружения.
- Maven - сборка Java-проектов.

## Джобы в Jenkins

### jobs_uploader - служебная

Читает YAML-конфиги из папки jobs/ и создаёт или обновляет все остальные джобы через Jenkins Job Builder. Запускать её нужно при первом развёртывании и после изменения любого YAML-файла в jobs/.

### ui_tests - UI-тесты

- Параметры: BRANCH, BROWSER (chrome/firefox), HEADLESS, BASE_URL, SELENOID_URL.
- Что делает: запускает Selenium-тесты через Selenoid.
- Allure: включает видео прогона каждой сессии.

### mobile_tests - мобильные тесты

- Параметры: BRANCH, APK_URL.
- Что делает: скачивает APK, поднимает Android-эмулятор и Appium через Docker Compose, запускает тесты.
- Allure: отчёт с шагами и результатами.

### api_tests - API-тесты

- Параметры: BRANCH (по умолчанию homework_3).
- Что делает: запускает RestAssured-тесты на WireMock-заглушки.
- Внутри: JsonSchemaValidation, Cucumber BDD-сценарии, мок падающего метода.
- Allure: отчёт со всеми шагами.

### running_tests - общая джоба

- Параметры: RUN_UI, RUN_MOBILE, RUN_API (все по умолчанию true).
- Что делает: запускает выбранные джобы параллельно.
- В конце: сводка статусов всех джоб.

## Запуск Jenkins

Шаг 1. Клонируйте репозиторий:

    git clone https://github.com/VladimirBelaz/jenkins-ci-cd.git
    cd jenkins-ci-cd

Шаг 2. Запустите Jenkins через Docker Compose:

    docker compose up -d --build

Шаг 3. Получите пароль для входа:

    docker exec -it jenkins cat /var/jenkins_home/secrets/initialAdminPassword

Шаг 4. Откройте Jenkins в браузере: http://localhost:8080

## Настройка Jenkins при первом запуске

Шаг 1. Добавьте credentials.

Manage Jenkins → Credentials → добавьте два типа:

- github (Username with password) - логин и GitHub Personal Access Token.
- jenkins_api (Username with password) - логин и пароль от Jenkins.

Шаг 2. Настройте Allure Commandline.

Manage Jenkins → Tools → Allure Commandline → Add:

- Name: allure
- Install automatically: галка, версия 2.29.0 или новее.

Шаг 3. Запустите jobs_uploader.

Откройте джобу jobs_uploader → Build with Parameters → Build.
Она создаст все остальные джобы: ui_tests, mobile_tests, api_tests, running_tests.

## Запуск тестов

### UI-тесты (ui_tests)

1. Выберите браузер chrome или firefox.
2. Включите или отключите headless-режим.
3. Нажмите Build.
4. После завершения откроется Allure-отчёт с видео.

### Mobile-тесты (mobile_tests)

1. Укажите APK_URL (по умолчанию APK из репозитория wishlist-mobile-tests).
2. Нажмите Build.
3. Jenkins скачает APK, поднимет Android-эмулятор и Appium.
4. После завершения отчёт будет доступен в Allure.

### API-тесты (api_tests)

1. Убедитесь, что BRANCH = homework_3.
2. Нажмите Build.
3. После завершения отчёт будет доступен в Allure.

### Все тесты одной кнопкой (running_tests)

1. Выберите, какие тесты запускать (UI, Mobile, API - можно все три).
2. Нажмите Build.
3. Джоба запустит выбранные параллельно и покажет итоговый статус.

## Триггеры

Все джобы настроены на два триггера:

- Poll SCM: H/5 * * * * - опрос репозитория каждые 5 минут (запуск по push).
- Build periodically: H 0 * * * - каждый день в полночь.

## Allure-отчёты

Allure-отчёты автоматически генерируются после каждой сборки и доступны
по ссылке Allure Report в боковом меню джобы.

UI-тесты дополнительно содержат видео прогона - в отчёте под каждым
тестом есть блок Video с плеером.

## Переменные и кастомизация

Для UI-тестов:

- SELENOID_URL - адрес Selenoid (по умолчанию http://host.docker.internal:4444).
- SELENOID_UI_URL - адрес Selenoid UI для видео (по умолчанию http://localhost:8081).

Для Mobile-тестов:

- APK_URL - ссылка на APK.
- ANDROID_EMULATOR - образ эмулятора (по умолчанию Android 12).

## Структура репозитория

    jenkins-ci-cd/
      jobs/                    YAML-конфиги джоб для JJB
        jobs_uploader.yaml
        ui_tests.yaml
        mobile_tests.yaml
        api_tests.yaml
        running_tests.yaml
      pipeline/                Groovy-пайплайны инфраструктурных джоб
        jobs_uploader.groovy
        run_tests.groovy
      Dockerfile               Образ Jenkins с JJB, Allure, Docker CLI
      docker-compose.yml       Запуск Jenkins
      README.md

## Полезные команды

Запустить Jenkins:

    docker compose up -d

Остановить Jenkins:

    docker compose stop

Посмотреть логи:

    docker compose logs -f jenkins

Зайти внутрь контейнера:

    docker compose exec jenkins bash

Применить изменения в YAML:

После правки jobs/*.yaml запустить джобу jobs_uploader в Jenkins.
