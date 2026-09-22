pipeline {
    agent any

    parameters {
        string(name: 'BRANCH', defaultValue: 'main')
        string(name: 'APK_URL', defaultValue: 'https://example.com/app.apk')
        string(name: 'TEST_DEVICES', defaultValue: '["Android 12","Android 14"]')
    }

    stages {
        stage('Checkout infrastructure repo (for ansible)') {
            steps {
                git branch: 'main',
                        credentialsId: 'github',
                        url: 'https://github.com/VladimirBelaz/jenkins-ci-cd.git'
            }
        }

        stage('Run Mobile tests via Ansible') {
            steps {
                sh """
                    ansible-playbook -i ansible/inventory.ini ansible/mobile-tests.yml \
                        -e branch='${params.BRANCH}' \
                        -e apk_url='${params.APK_URL}' \
                        -e '{"devices": ${params.TEST_DEVICES}}'
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