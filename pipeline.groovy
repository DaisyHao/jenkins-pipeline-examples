stage 'prepare the pipeline'
node() {
	
    git poll: false, changelog: false, url: pipelineRepo, credentialsId: "git-credentials", branch: pipelineBranch

    utilities = load "lib/utilities.groovy"
    cloudfoundry = load "lib/cloudfoundry.groovy"
    shell = load "lib/shell.groovy"
    git = load "lib/git.groovy"
    pom = load "lib/pom.groovy"
    
}

stage 'maven build'
node() {
    git url: repositoryUrl, credentialsId: "git-credentials", branch: branch
    artifactId = pom.artifactId(pwd() + "/pom.xml")
    version = pom.version(pwd() + "/pom.xml")
    majorVersion = pom.majorVersion(pwd() + "/pom.xml")
    uniqueVersion = version + "." + utilities.timestamp()
    appName = "${artifactId}-${version}".replace(".", "-")
    artifactPath = "target/${artifactId}-${uniqueVersion}.jar"
    commitId = shell.pipe("git rev-parse HEAD")
    bat "mvn -DnewVersion=${uniqueVersion} versions:set"
    bat "mvn package"
    stash includes: "${artifactPath}", name: 'app-artifact'
}
