pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8.5'  // Name of Maven installed in Jenkins Global Tool Configuration
    }

    environment {
        // Define the environment variables for SonarQube
        SONARQUBE_URL = 'http://localhost:9000'  // URL of your SonarQube instance
        SONARQUBE_TOKEN = '85b44c83e6648af619c6423c18eb4f964ef0defa'     // SonarQube token for authentication
        SONARQUBE_SCANNER = 'SonarPipeLine'  // Name of Sonar Scanner configured in Jenkins
    }

    stages {

        // Stage 1: Checkout code from Git repository
        stage('Checkout') {
            steps {
                git branch:'master', url: 'https://github.com/vipulkumar1804/Jenkins_NAGP.git'
            }
        }

        // Stage 2: Build the project using Maven
        stage('Build') {
            steps {
                script {
                    echo 'Building the project...'
                    sh 'mvn clean install'
                }
            }
        }

        // Stage 3: Unit Testing and Test Reports
        stage('Run Unit Tests') {
            steps {
                script {
                    echo 'Running unit tests...'
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit '/test-output/*.xml'  // Collect test results (JUnit reports)
                }
            }
        }

        // Stage 4: SonarQube Analysis
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {  // Configure SonarQube environment in Jenkins
                    sh """
                    mvn sonar:sonar \
                        -Dsonar.projectKey=your-project-key \
                        -Dsonar.host.url=${SONARQUBE_URL} \
                        -Dsonar.login=${SONARQUBE_TOKEN}
                    """
                }
            }
        }

        // Stage 5: Quality Gate
        stage('SonarQube Quality Gate') {
            steps {
                timeout(time: 1, unit: 'MINUTES') {
                    script {
                        def qualityGate = waitForQualityGate()
                        if (qualityGate.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qualityGate.status}"
                        }
                    }
                }
            }
        }
    }

    // Post-build actions
    post {
        success {
            echo 'Build and SonarQube analysis completed successfully.'
        }

        failure {
            echo 'Build or SonarQube analysis failed.'
        }

        always {
            // Optionally, add any cleanup tasks here
        }
    }
}
