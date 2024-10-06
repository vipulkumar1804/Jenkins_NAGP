pipeline{
    agent any
    stages{
        stage('Rest Assured'){
        steps{
            echo 'Running Rest APIs'
            git branch: 'master', url: 'https://github.com/vipulkumar1804/Jenkins_NAGP.git'
        }
    
    }
    stage('Test'){
        steps{
               echo 'Testing APIs'
               bat 'mvn clean test'}
        } 
    }
    }