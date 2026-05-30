pipeline {
    agent any
    
    tools {
        // Tells Jenkins to inject the managed JDK 23 tool path into this runtime
        jdk 'jdk23'
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }
        stage('Build Maven Project') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }
        stage('Build Docker Image') {
            steps {
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
            echo 'Pipeline Executed Successfully!'
        }
        failure {
            echo 'Pipeline Failed.'
        }
    }
}