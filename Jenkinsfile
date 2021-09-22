@Library('indus-common-libs')
import com.lyra.indus.preintelib.*

def namespace = 'vad'
def project = 'sdk-server'
def label = "${project}-${UUID.randomUUID().toString()}"

def slaveTemplates = new PodTemplates()
slaveTemplates.preinteTemplate(namespace) {
    
    try {
        
        podTemplate(
                name: label,
                label: label,
                namespace: namespace,
                containers: [
                        // maven container
                        containerTemplate(name: 'maven', image: 'ci/maven-docker:latest', ttyEnabled: true, command: 'cat', alwaysPullImage: true, resourceRequestCpu: '200m',privileged: true,
                                envVars: [
                                        // With this logger, any mvn command in bashmode (-B) will be quiet relating to downloads (console no longer flooded by 'sizes in KB')
                                        envVar(key: 'MAVEN_OPTS', value: '-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn')
                                ])
                            ],
                volumes: [
                        // Maven repo caching
                        hostPathVolume(mountPath: '/opt/lyra/app-root/src/maven_repo', hostPath: '/opt/lyra/volume/maven')
                ]) {
                    
                    properties([parameters([
                        booleanParam(name: 'Publish', defaultValue: false, description: 'Execute a publish on Nexus?'),
                        booleanParam(name: 'Quality', defaultValue: true, description: 'Execute a quality analysis?'),
                        string(defaultValue: "develop", description: '', name: 'GIT_BRANCH')
                    ])])
            
            node(label) {   
                stage('Git checkout') {
                    // Get some code from a Gitlab repository
                    // No credential because cloned from public repo
                    git branch: this.GIT_BRANCH, url: 'https://github.com/lyra/rest-api-server-java-sdk.git'
                }
    
                stage('Building') {
                    // Run the maven build
                    // There is not tests implemented
                    container(name:'maven', shell:'/bin/bash') {
                        sh "mvn clean package"
        			}
                }
               
                stage('SonarQube analysis') {
                    if (Boolean.valueOf(this.quality)) {
                        container(name:'maven', shell:'/bin/bash') {
                            withSonarQubeEnv('SonarQube Lyra') {
                                sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar -Dsonar.projectKey=pole-mobile:sdk-server -Dsonar.projectName=pole-mobile:sdk-server -Dsonar.working.directory=.sonar"
                            }
                        }
                    } else {
                        print "Quality analysis skipped!"
                    }
                }
                
                stage("Check SonarQube Quality Gate"){
                    if (Boolean.valueOf(this.quality)) {
                        container(name:'maven', shell:'/bin/bash') {
                            sh "env"
                            // Wait for Sonar quality gate and fail the build if in error
                            checkSonarQualityGate([
                                reportFile: '.sonar/report-task.txt'
                            ])
                        }
                    } else {
                        print "Quality analysis skipped!"
                    }
                }
                
                stage('Publish on Nexus') { 
                    if (Boolean.valueOf(this.publish)) {
                        container(name:'maven', shell:'/bin/bash') {
                            // Push jar
                            sh "mvn clean deploy -Pnexus"
                        }
                    } else {
                        print "Pubishing skipped!"
                    }
                }
            }
            
        }
    } catch (Exception e) {
        rocketSend webhookTokenCredentialId:'rocketchat-token-jenkins-bot', 
            channel: "mobile-dev",
            avatar: 'http://mirrors.jenkins-ci.org/art/jenkins-logo/favicon.ico',
            message: "Build Failed ! ${env.JOB_NAME} (<${env.BUILD_URL}|Open>)"
        throw e
    }
}
