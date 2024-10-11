pipeline {
    agent any

    environment {
        // Define environment variables
        SONAR_URL = 'http://localhost:9000'
        SONAR_TOKEN = '85b44c83e6648af619c6423c18eb4f964ef0defa'
        SONAR_SCANNER = 'Freestyle'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch:'master', url: 'https://github.com/vipulkumar1804/Jenkins_NAGP.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean'
            }
        }
       
         stage('unit test'){
              steps{
                echo 'Testing APIs'
               bat 'mvn test'
         }
       }    

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') { 
                    sh 'mvn sonar:sonar -Dsonar.host.url=${SONAR_URL} -Dsonar.login=${SONAR_TOKEN}'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    timeout(time: 1, unit: 'MINUTES') {
                        def qualitygate = waitForQualityGate()
                        if (qualitygate.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qualitygate.status}"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit '**/test-output/*.xml'
        }

        success {
            echo 'Build and SonarQube analysis completed successfully!'
        }

        failure {
            echo 'Build or SonarQube analysis failed.'
        }
    }
}
