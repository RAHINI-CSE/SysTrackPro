pipeline {
    agent any

    stages {
        stage('Checkout Source') {
            steps {
                // Securely pulls your multi-module project files from GitHub
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Uses the Native Jenkins API to execute your multi-stage Dockerfile build process
                    docker.build('systrackpro:latest', '.')
                }
            }
        }

        stage('Deploy Container') {
            steps {
                // Force removes any running naming-conflict objects safely before launching
                sh 'docker rm -f systrackpro-container || true'
                
                script {
                    // Natively provisions and runs the newly compiled image on host port 8080
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