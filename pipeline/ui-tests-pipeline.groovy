pipeline {
    agent any

    tools {
        maven 'Maven-3'  // Если Maven установлен на агенте
    }

    parameters {
        string(name: 'BRANCH', defaultValue: 'main')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'])
        booleanParam(name: 'HEADLESS', defaultValue: true)
        string(name: 'BASE_URL', defaultValue: 'https://otus.ru')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run tests') {
            steps {
                sh """
                    mvn clean test \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -Dbase.url=${params.BASE_URL}
                """
            }
        }

        stage('Publish Allure report') {
            steps {
                allure([
                        includeProperties: false,
                        results: [[path: 'target/allure-results']]
                ])
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/surefire-reports/*', allowEmptyArchive: true
        }
    }
}