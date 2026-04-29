pipeline {

    // Run on any available Jenkins agent
    agent any

    // Tool defined in Jenkins → Manage Jenkins → Tools
    tools {
        maven 'Maven-3.9'
    }

    // Environment variables available to all stages
    environment {
        APP_NAME     = 'securebank-microservices'
        JAVA_VERSION = '17'
    }

    // Trigger: poll GitHub every minute (we'll upgrade to webhook later)
    triggers {
        pollSCM('* * * * *')
    }

    stages {

        stage('Checkout') {
            steps {
                echo '🔄 Checking out source code...'
                checkout scm   // Checks out from the configured GitHub repo
                echo "✅ Checked out branch: ${env.BRANCH_NAME}"
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Building project...'
                sh 'mvn clean compile -DskipTests'
            }
            post {
                success { echo '✅ Build successful!' }
                failure { echo '❌ Build failed!' }
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Running tests...'
                sh 'mvn test'
            }
            post {
                always {
                    // Publish JUnit test results in Jenkins UI
                    junit(
                        testResults: '**/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
                success { echo '✅ All tests passed!' }
                failure { echo '❌ Tests failed!' }
            }
        }

        stage('Package') {
            steps {
                echo '📦 Packaging application...'
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    echo '✅ JAR created successfully!'
                    // Archive the JAR so it's downloadable from Jenkins UI
                    archiveArtifacts artifacts: '**/target/*.jar',
                                     allowEmptyArchive: true
                }
            }
        }

        stage('Code Quality Check') {
            steps {
                echo '🔍 Checking code quality...'
                // Verify no test failures slipped through
                sh 'mvn verify -DskipTests'
            }
        }
    }

    // Runs after ALL stages complete — regardless of success or failure
    post {
        success {
            echo """
            ╔══════════════════════════════════╗
            ║   ✅ PIPELINE SUCCEEDED          ║
            ║   ${APP_NAME}                    ║
            ║   Branch: ${env.BRANCH_NAME}     ║
            ╚══════════════════════════════════╝
            """
        }
        failure {
            echo """
            ╔══════════════════════════════════╗
            ║   ❌ PIPELINE FAILED             ║
            ║   ${APP_NAME}                    ║
            ║   Check logs above for details   ║
            ╚══════════════════════════════════╝
            """
        }
        always {
            echo '🧹 Cleaning up workspace...'
            cleanWs()  // Clean workspace after every build
        }
    }
}