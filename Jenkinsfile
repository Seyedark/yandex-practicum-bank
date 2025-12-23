pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = credentials('DOCKER_REGISTRY')
        GITHUB_USERNAME = credentials('GITHUB_USERNAME')
        IMAGE_TAG       = "latest"
        NAMESPACE       = "test"
        SERVICES        = "account,blocker,cash,exchange,front,generator,notification,transfer"
    }

    stages {
        stage('Build, Test & Package') {
            matrix {
                agent any
                axes {
                    axis {
                        name 'SERVICE'
                        values "${env.SERVICES}".split(',')
                    }
                }
                stages {
                    stage('Maven Build') {
                        steps {
                            dir(SERVICE) {
                                sh 'mvn clean install "-Dtest=!*ContractTest"'
                            }
                        }
                    }
                    stage('Docker Build') {
                        steps {
                            sh "docker build -t ${SERVICE}:${IMAGE_TAG} ${SERVICE}"
                        }
                    }
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                withCredentials([string(credentialsId: 'GHCR_TOKEN', variable: 'GHCR_TOKEN')]) {
                    sh '''
                        echo $GHCR_TOKEN | docker login ghcr.io -u ${GITHUB_USERNAME} --password-stdin
                    '''
                }
            }

            matrix {
                agent any
                axes {
                    axis {
                        name 'SERVICE'
                        values "${env.SERVICES}".split(',')
                    }
                }
                steps {
                    sh """
                        docker tag ${SERVICE}:${IMAGE_TAG} ghcr.io/${GITHUB_USERNAME}/${SERVICE}:${IMAGE_TAG}
                        docker push ghcr.io/${GITHUB_USERNAME}/${SERVICE}:${IMAGE_TAG}
                    """
                }
            }
        }

        stage('Helm deploy') {
            steps {
                sh """
                    helm upgrade --install full-app ./umbrella-chart \\
                      --namespace ${NAMESPACE} \\
                      --create-namespace
                """
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}