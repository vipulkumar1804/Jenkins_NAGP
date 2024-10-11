pipeline {
    agent any

    environment {
        // Define environment variables
        SONAR_URL = 'http://localhost:9000'
        SONAR_TOKEN = 'e828a6df74baaac467b2c9989d5f0b10f7d572d2'
        SONAR_SCANNER = 'NAGP_SonarQube'
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
                withSonarQubeEnv('NAGP_SonarQube') { 
                    bat "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar"
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
            junit '**/test-output/junitreports/*.xml'
        }

        success {
            echo 'Build and SonarQube analysis completed successfully!'
        }

        failure {
            echo 'Build or SonarQube analysis failed.'
        }
    }
}
