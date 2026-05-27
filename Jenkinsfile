pipeline {
    agent any

    stages {
        stage('Checkout Source') {
            steps {
                // Pulls the latest code from your repository
                checkout scm
            }
        }

        stage('Build Maven Project') {
            steps {
                // Ensure the Linux container has permission to execute the Maven wrapper
                sh 'chmod +x mvnw'
                // Compile and package the application while skipping unit tests for speed
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Build the image and tag it as systrackpro:latest
                sh 'docker build -t systrackpro:latest .'
            }
        }

        stage('Deploy Container') {
            steps {
                // Force remove any existing container with the same name to prevent naming conflicts
                sh 'docker rm -f systrackpro-container || true'
                
                // Run the new container, mapping host port 8080 to container port 8080
                sh 'docker run -d -p 8080:8080 --name systrackpro-container systrackpro:latest'
            }
        }
    }

    post {
        success {
            echo 'Pipeline Executed Successfully! SysTrackPro is up and running.'
        }
        failure {
            echo 'Pipeline Failed. Please check the Jenkins console logs for details.'
        }
    }
}