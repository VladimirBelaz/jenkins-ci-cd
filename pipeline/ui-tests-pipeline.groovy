pipeline {
    agent any

    tools {
        maven 'Maven-3'
    }

    parameters {
        string(name: 'BRANCH', defaultValue: 'main')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'])
        booleanParam(name: 'HEADLESS', defaultValue: true)
        string(name: 'BASE_URL', defaultValue: 'https://otus.ru')
        string(name: 'TEST_COMPONENTS', defaultValue: '["login","cart"]')
    }

    stages {
        stage('Checkout infrastructure repo (for ansible)') {
            steps {
                git branch: 'main',
                        credentialsId: 'github',
                        url: 'https://github.com/VladimirBelaz/jenkins-ci-cd.git'
            }
        }

        stage('Run tests via Ansible') {
            steps {
                sh """
                    ansible-playbook -i ansible/inventory.ini ansible/ui-tests.yml \
                        -e branch='${params.BRANCH}' \
                        -e browser='${params.BROWSER}' \
                        -e headless='${params.HEADLESS}' \
                        -e base_url='${params.BASE_URL}' \
                        -e '{"components": ${params.TEST_COMPONENTS}}'
                """
            }
        }

        stage('Publish Allure report') {
            steps {
                allure([
                        includeProperties: false,
                        results: [[path: 'allure-results']]
                ])
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'allure-results/**', allowEmptyArchive: true
        }
    }
}