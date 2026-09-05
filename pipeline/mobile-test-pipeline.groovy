pipeline {
    agent any

    parameters {
        string(name: 'BRANCH', defaultValue: 'main')
        string(name: 'APK_URL', defaultValue: 'https://example.com/app.apk')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Download APK') {
            steps {
                sh "wget -O app.apk ${params.APK_URL}"
            }
        }

        stage('Run Appium tests') {
            steps {
                sh """
                    # Поднимаем эмулятор и Appium в Docker (упрощённо)
                    docker run -d --name appium -p 4723:4723 \\
                        -v \$(pwd):/tests \\
                        appium/appium:latest \\
                        --allow-insecure chromedriver_autodownload
                    sleep 10
                    gradle clean test -Dapk.path=./app.apk
                """
            }
        }

        stage('Publish Allure report') {
            steps {
                allure([
                        includeProperties: false,
                        results: [[path: 'build/allure-results']]
                ])
            }
        }
    }

    post {
        always {
            sh "docker rm -f appium || true"
        }
    }
}