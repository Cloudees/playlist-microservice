def incrementDataSeedJobVersion(){
    echo "Incrementing the Application Version"
    def currentVersion = sh(script: "grep 'const version' app.go | awk '{print \$NF}' | tr -d '\"'", returnStdout: true).trim()
    // Incrementing the Version
    def newVersion = incrementVersion(currentVersion)
    // Updating the Version in the Source Code
    sh "sed -i 's/const version = \"$currentVersion\"/const version = \"$newVersion\"/' app.go"
    // Commit the Changes
    sh "git remote add oumayma git@github.com:Cloudees/playlist-microservice.git"
    sh "git checkout main"
    sh "git commit -am 'Increment Version to $newVersion'"
    // Setting the New Version as an Environment Variable for Later Use
    env.IMAGE_VERSION = newVersion
}

def incrementVersion(currentVersion) {
    def versionParts = currentVersion.split("\\.")
    def newPatchVersion = versionParts[2].toInteger() + 1
    return "${versionParts[0]}.${versionParts[1]}.$newPatchVersion"
}

def buildGoBinary() {
    echo "Compiling and Building the Application..."
    sh "go build -o playlist-microservice-${IMAGE_VERSION}"
}

def buildDockerImage() {
    echo "Building the Docker Image..."
    sh "docker build -t oumaymacharrad/playlist-microservice:${IMAGE_VERSION} ."
}

def pushToDockerHub() {
    withCredentials([usernamePassword(credentialsId: "Docker-Hub-Credentials", passwordVariable: "PASS", usernameVariable: "USER")]) {
        echo "Pushing the Docker Image to Docker Hub..."
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh "docker push oumaymacharrad/playlist-microservice:${IMAGE_VERSION}"
    }
}

def trivyScan(){
    echo "Running Trivy Security Scan..."
    sh "trivy image --format template --template '@/usr/local/share/trivy/templates/html.tpl' -o TrivyReport.html oumaymacharrad/playlist-microservice:${IMAGE_VERSION} --scanners vuln"
}

def pushToDeploymentGitHub() {
    echo "Pushing to Deployment GitHub..."
    sh "git clone git@github.com:Cloudees/deployment.git"
    def currentVersion = sh(script: "grep 'image: oumaymacharrad/playlist-microservice' deployment/microservices/playlist/deployment-playlist.yaml | awk -F: '{print \$3}' | cut -d'@' -f1", returnStdout: true).trim()
    env.CURRENT_VERSION = currentVersion
    sh "sed -i 's|image: oumaymacharrad/playlist-microservice:$CURRENT_VERSION|image: oumaymacharrad/playlist-microservice:$IMAGE_VERSION|' deployment/microservices/playlist/deployment-playlist.yaml"
    sh """
    cd deployment
    git commit -am 'Increment Version to ${IMAGE_VERSION}'
    """
    sshagent(credentials: ['Private-Key']) {
        sh """
        cd deployment
        git push origin main
        """
    }
}

def gitpush(){
    // Push the Changes to GitHub
    sshagent (credentials: ["Private-Key"]) {
        sh "git push oumayma main"
        sh "git remote remove oumayma"
    }
}

return this
