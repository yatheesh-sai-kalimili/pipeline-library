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
            sh 'export MAVEN_HOME=/opt/maven'
            sh 'export PATH=$PATH:$MAVEN_HOME/bin'
            sh 'mvn --version'
            sh 'mvn clean package'
        }
        }
    }
}
}