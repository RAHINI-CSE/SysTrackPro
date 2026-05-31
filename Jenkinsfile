pipeline {
    agent any
    
    tools {
        // Must match the exact name you just saved in the Global Tool configuration screen
        dockerTool 'docker-default'
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Triggers your multi-stage Dockerfile using Jenkins' native plugin API
                    docker.build('systrackpro:latest', '.')
                }
            }
        }

        stage('Deploy Container') {
            steps {
                // Safely tears down any old containers running under this name first
                sh 'docker rm -f systrackpro-container || true'
                
                script {
                    // Launches your Java 23 workspace container live on port 8080
                    docker.image('systrackpro:latest').run('-d -p 8080:8080 --name systrackpro-container')
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline Executed Successfully! SysTrackPro is up.'
        }
        failure {
            echo 'Pipeline Failed.'
        }
    }
}