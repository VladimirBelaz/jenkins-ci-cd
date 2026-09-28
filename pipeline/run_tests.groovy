pipeline {
    agent any

    parameters {
        booleanParam(name: 'RUN_UI', defaultValue: true, description: 'Run ui_tests')
        booleanParam(name: 'RUN_MOBILE', defaultValue: true, description: 'Run mobile_tests')
        booleanParam(name: 'RUN_API', defaultValue: true, description: 'Run api_tests')
    }

    stages {
        stage('Run selected tests') {
            steps {
                script {
                    def branches = [:]
                    def results = [:]

                    if (params.RUN_UI) {
                        branches['UI tests'] = {
                            def result = build job: 'ui_tests', wait: true, propagate: false
                            results['ui_tests'] = result.result
                        }
                    }

                    if (params.RUN_MOBILE) {
                        branches['Mobile tests'] = {
                            def result = build job: 'mobile_tests', wait: true, propagate: false
                            results['mobile_tests'] = result.result
                        }
                    }

                    if (params.RUN_API) {
                        branches['API tests'] = {
                            def result = build job: 'api_tests', wait: true, propagate: false
                            results['api_tests'] = result.result
                        }
                    }

                    if (branches.isEmpty()) {
                        error("Ни один параметр не выбран — нечего запускать")
                    }

                    parallel branches

                    echo "=== ИТОГИ ==="
                    results.each { name, result ->
                        echo "${name}: ${result}"
                    }

                    def failed = results.findAll { it.value != 'SUCCESS' }
                    if (!failed.isEmpty()) {
                        error("Упали джобы: ${failed.keySet().join(', ')}")
                    }
                }
            }
        }
    }

    post {
        always {
            echo "running_tests pipeline finished"
        }
    }
}