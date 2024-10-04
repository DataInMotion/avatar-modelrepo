pipeline  {
    agent any

	environment {
		imagename = 'avatar/ma'
		dockerImage = ''
		JAVA_OPTS = "-Xms4096m -Xmx4096m -XX:MaxMetaspaceSize=1024m ${sh(script:'echo $JAVA_OPTS', returnStdout: true).trim()}"
	}

    tools {
        jdk 'OpenJDK17'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    
    stages {
    
    	stage('clean workspace and checkout') {
			steps {
				cleanWs()
				checkout scm
			}
		}
    
    	stage('Build') {

			steps {
				echo "I am building app on branch: ${env.GIT_BRANCH}"

				sh "./gradlew clean build -x testOSGi --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"
			}
		}
		
		stage('Snapshot Integration Tests') {

			when { 
                branch 'develop' 
            }

			steps {
				echo "I am running integration tests on branch: ${env.GIT_BRANCH}"

				script {
				
					sh "./gradlew testOSGi --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"

					junit '**/generated/test-reports/**/*.xml'
				}
			}
		}
    
        
        stage('Snapshot branch release') {
            when { 
                branch 'develop'
            }
            steps  {
                echo "I am building on ${env.JOB_NAME}"
                sh "./gradlew release --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"
                sh "mkdir -p $JENKINS_HOME/repo.gecko/snapshot/DataInMotion/MA"
                sh "rm -rf $JENKINS_HOME/repo.gecko/snapshot/DataInMotion/MA/*"
                sh "cp -r cnf/release/* $JENKINS_HOME/repo.gecko/snapshot/DataInMotion/MA"
            }
        }
        
        stage('Resolve Application'){

			steps  {
				echo "I am exporting applications on branch: ${env.GIT_BRANCH}"

                sh "./gradlew de.avatar.ma.runtime:resolve.modelatlas.runtime_base --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"
			}
		}

        stage('Export Application'){

			steps  {
				echo "I am exporting applications on branch: ${env.GIT_BRANCH}"

                sh "./gradlew de.avatar.ma.runtime:export.modelatlas.runtime_docker --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"
			}
		}

        stage('Prepare Docker'){
			when {
				branch 'main'
			}

			steps  {
				echo "I am building and publishing a docker image on branch: ${env.GIT_BRANCH}"
    			sh "./gradlew prepareDocker --info --stacktrace -Dmaven.repo.local=${WORKSPACE}/.m2"

			}
		}

        stage('Docker image build'){
			when {
				branch 'main'
			}

			steps  {
				echo "I am building and publishing a docker image on branch: ${env.GIT_BRANCH}"
    			
				step([$class: 'DockerBuilderPublisher',
				      dockerFileDirectory: 'docker',
							cloud: 'docker',
							tagsString: 'devel.data-in-motion.biz:6000/avatar/ma:latest',
							pushOnSuccess: true,
							pushCredentialsId: 'dim-nexus'])

			}
		}
	}
}
