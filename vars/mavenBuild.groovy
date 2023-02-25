#!/usr/bin/env groovy
def call (body) {
   def args = [
       branch: '',
       url: '',
       service: '',
       buildType: ''
  ]

body.resolveStrategy = Closure.DELEGATE_FIRST
body.delegate = args
body ()
echo "INFO: ${args.service}"
echo "INFO: ${args.branch}"

pipeline {
   agent any
   tools {
        maven 'maven'
    }
   stages {
      stage('Git Checkout') {
        steps {
           checkout([
              $class: 'GitSCM',
              branches: [[name: args.branch ]],
              userRemoteConfigs: [[ url: args.url ]]
              ])
              }
        }
       stage ('Build') {
        when{
             equals(actual: args.buildType, expected: "Java build")
        }
        steps {
            sh 'mvn clean package'
        }
        }
    }
     post {
        always {
           junit '**/target/surefire-reports/TEST-*.xml'
            archiveArtifacts 'target/*.jar'
        }
    }
}
}