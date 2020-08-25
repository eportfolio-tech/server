pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'sh \'./gradlew assemble\''
      }
    }

    stage('Test') {
      steps {
        sh 'sh \'./gradlew test\''
      }
    }

  }
}