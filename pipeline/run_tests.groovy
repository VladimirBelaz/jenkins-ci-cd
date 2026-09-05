pipeline {
    agent any

    parameters {
        booleanParam(name: 'RUN_UI', defaultValue: true)
        booleanParam(name: 'RUN_MOBILE', defaultValue: true)
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

                    parallel branches
                }
            }
        }
    }
}