pipeline {
    agent any

    environment {
        IMAGE_NAME = "dilipkamti/user_management"
        DOCKER_TAG_PREFIX = "v"
    }


    parameters {
        choice(name: 'PROFILE', choices: ['dev', 'prod'], description: 'Choose Spring Boot profile')
        booleanParam(name: 'DELETE_OLD_BUILDS', defaultValue: false, description: 'Delete old Docker containers/images before building?')
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/DilipKamti/user_management.git'
            }
        }

        stage('Determine Docker Image Version') {
            steps {
                script {
                    def versionFile = '.docker-version'
                    def currentVersion = '0.0'
                    if (fileExists(versionFile)) {
                        currentVersion = readFile(versionFile).trim()
                    }
                    def (major, minor) = currentVersion.tokenize('.').collect { it.toInteger() }
                    def newVersion = "${major}.${minor + 1}"
                    def versionTag = "${DOCKER_TAG_PREFIX}${newVersion}"
                    env.DOCKER_VERSION = versionTag
                    writeFile file: versionFile, text: newVersion
                }
            }
        }

        stage('Clean Old Docker Resources') {
            when {
                expression { params.DELETE_OLD_BUILDS }
            }
            steps {
                script {
                    if (isUnix()) {
                        sh """
                            docker ps -a --filter "ancestor=${IMAGE_NAME}" --format "{{.ID}}" | xargs -r docker stop || true
                            docker ps -a --filter "ancestor=${IMAGE_NAME}" --format "{{.ID}}" | xargs -r docker rm || true
                            docker images ${IMAGE_NAME} --format "{{.Repository}}:{{.Tag}}" | grep -v ${DOCKER_VERSION} | xargs -r docker rmi -f || true
                        """
                    } else {
                        bat """
                        for /f "delims=" %%i in ('docker ps -a --filter "ancestor=${IMAGE_NAME}" --format "{{.ID}}"') do (
                            docker stop %%i
                            docker rm %%i
                        )

                        powershell -Command "docker images ${IMAGE_NAME} --format '{{.Repository}}:{{.Tag}}' | Where-Object { \$_ -ne '${IMAGE_NAME}:${DOCKER_VERSION}' } | ForEach-Object { docker rmi -f \$_ }"
                        """
                    }
                }
            }
        }

        stage('Build Maven Project') {
            steps {
                script {
                    def mvnCmd = "mvn clean package -DskipTests -Dspring.profiles.active=${params.PROFILE}"
                    if (isUnix()) {
                        sh mvnCmd
                    } else {
                        bat mvnCmd
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def versionTag = "${IMAGE_NAME}:${DOCKER_VERSION}"
                    def buildCmd = "docker build -t ${versionTag} ."
                    if (isUnix()) {
                        sh buildCmd
                    } else {
                        bat buildCmd
                    }
                }
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    script {
                        if (isUnix()) {
                            sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"
                        } else {
                            bat """echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USERNAME% --password-stdin"""
                        }
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    def versionTag = "${IMAGE_NAME}:${DOCKER_VERSION}"
                    if (isUnix()) {
                        sh "docker push ${versionTag}"
                    } else {
                        bat "docker push ${versionTag}"
                    }
                }
            }
        }

        stage('Deploy to Production (Optional)') {
            when {
                expression { params.PROFILE == 'prod' }
            }
            steps {
                echo "Deploying user_management in production mode with tag: ${DOCKER_VERSION}"
                // SSH or docker-compose logic can go here
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "✅ Build and deployment successful with Docker tag: ${DOCKER_VERSION}"
        }
        failure {
            echo "❌ Build or deployment failed!"
        }
    }
}
