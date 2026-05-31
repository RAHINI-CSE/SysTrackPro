pipeline {
    agent any

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                // This single command now securely handles both Maven packaging and Image creation!
                sh 'docker build -t systrackpro:latest .'
            }
        }

        stage('Deploy Container') {
            steps {
                sh 'docker rm -f systrackpro-container || true'
                sh 'docker run -d -p 8080:8080 --name systrackpro-container systrackpro:latest'
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