pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = credentials('DOCKER_REGISTRY')
        GITHUB_USERNAME = credentials('GITHUB_USERNAME')
        IMAGE_TAG       = "latest"
        NAMESPACE       = "test"
    }

    stages {
        stage('Build & Unit Tests') {
            parallel {
                stage('Account Service') {
                    steps {
                        dir('account') {
                            sh 'mvn clean install "-Dtest=!*ContractTest"'
                        }
                    }
                }
                stage('Blocker Service') {
                    steps {
                        dir('blocker') {
                            sh 'mvn clean install "-Dtest=!*ContractTest"'
                        }
                    }
                }
                stage('Cash Service') {
                    steps {
                        dir('cash') {
                            sh 'mvn clean install "-Dtest=!*ContractTest"'
                        }
                    }
                }
                stage('Exchange Service') {
                    steps {
                        dir('exchange') {
                            sh 'mvn clean install "-Dtest=!*ContractTest"'
                        }
                    }
                }
                stage('Front Service') {
                    steps {
                        dir('front') {
                            sh 'mvn clean install "-Dtest=!*ContractTest"'
                        }
                    }
                }
                stage('Generator Service') {
                    steps {
                        dir('generator') {
                            sh 'mvn clean install "-Dtest=!*ContractTest"'
                        }
                    }
                }
                stage('Notification Service') {
                    steps {
                        dir('notification') {
                            sh 'mvn clean install "-Dtest=!*ContractTest"'
                        }
                    }
                }
                stage('Transfer Service') {
                    steps {
                        dir('transfer') {
                            sh 'mvn clean install "-Dtest=!*ContractTest"'
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            parallel {
                stage('Build Account Docker') {
                    steps {
                        sh 'docker build -t account:${IMAGE_TAG} account'
                    }
                }
                stage('Build Blocker Docker') {
                    steps {
                        sh 'docker build -t blocker:${IMAGE_TAG} blocker'
                    }
                }
                stage('Build Cash Docker') {
                    steps {
                        sh 'docker build -t cash:${IMAGE_TAG} cash'
                    }
                }
                stage('Build Exchange Docker') {
                    steps {
                        sh 'docker build -t exchange:${IMAGE_TAG} exchange'
                    }
                }
                stage('Build Front Docker') {
                    steps {
                        sh 'docker build -t front:${IMAGE_TAG} front'
                    }
                }
                stage('Build Generator Docker') {
                    steps {
                        sh 'docker build -t generator:${IMAGE_TAG} generator'
                    }
                }
                stage('Build Notification Docker') {
                    steps {
                        sh 'docker build -t notification:${IMAGE_TAG} notification'
                    }
                }
                stage('Build Transfer Docker') {
                    steps {
                        sh 'docker build -t transfer:${IMAGE_TAG} transfer'
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

                parallel {
                    stage('Push Account') {
                        steps {
                            sh '''
                                docker tag account:${IMAGE_TAG} ghcr.io/${GITHUB_USERNAME}/account:${IMAGE_TAG}
                                docker push ghcr.io/${GITHUB_USERNAME}/account:${IMAGE_TAG}
                            '''
                        }
                    }
                    stage('Push Blocker') {
                        steps {
                            sh '''
                                docker tag blocker:${IMAGE_TAG} ghcr.io/${GITHUB_USERNAME}/blocker:${IMAGE_TAG}
                                docker push ghcr.io/${GITHUB_USERNAME}/blocker:${IMAGE_TAG}
                            '''
                        }
                    }
                    stage('Push Cash') {
                        steps {
                            sh '''
                                docker tag cash:${IMAGE_TAG} ghcr.io/${GITHUB_USERNAME}/cash:${IMAGE_TAG}
                                docker push ghcr.io/${GITHUB_USERNAME}/cash:${IMAGE_TAG}
                            '''
                        }
                    }
                    stage('Push Exchange') {
                        steps {
                            sh '''
                                docker tag exchange:${IMAGE_TAG} ghcr.io/${GITHUB_USERNAME}/exchange:${IMAGE_TAG}
                                docker push ghcr.io/${GITHUB_USERNAME}/exchange:${IMAGE_TAG}
                            '''
                        }
                    }
                    stage('Push Front') {
                        steps {
                            sh '''
                                docker tag front:${IMAGE_TAG} ghcr.io/${GITHUB_USERNAME}/front:${IMAGE_TAG}
                                docker push ghcr.io/${GITHUB_USERNAME}/front:${IMAGE_TAG}
                            '''
                        }
                    }
                    stage('Push Generator') {
                        steps {
                            sh '''
                                docker tag generator:${IMAGE_TAG} ghcr.io/${GITHUB_USERNAME}/generator:${IMAGE_TAG}
                                docker push ghcr.io/${GITHUB_USERNAME}/generator:${IMAGE_TAG}
                            '''
                        }
                    }
                    stage('Push Notification') {
                        steps {
                            sh '''
                                docker tag notification:${IMAGE_TAG} ghcr.io/${GITHUB_USERNAME}/notification:${IMAGE_TAG}
                                docker push ghcr.io/${GITHUB_USERNAME}/notification:${IMAGE_TAG}
                            '''
                        }
                    }
                    stage('Push Transfer') {
                        steps {
                            sh '''
                                docker tag transfer:${IMAGE_TAG} ghcr.io/${GITHUB_USERNAME}/transfer:${IMAGE_TAG}
                                docker push ghcr.io/${GITHUB_USERNAME}/transfer:${IMAGE_TAG}
                            '''
                        }
                    }
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