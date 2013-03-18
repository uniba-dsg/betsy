package betsy.data

import betsy.Configuration

abstract class Engine implements EngineAPI {

    AntBuilder ant = new AntBuilder()

    TestSuite testSuite

    final List<Process> processes = []

    void setAnt(AntBuilder ant) {
        this.ant = ant
    }

    /**
     * The path <code>src/main/xslt/$engine</code>
     *
     * @return the path <code>src/main/xslt/$engine</code>
     */
    String getXsltPath() {
        "src/main/xslt/${getName()}"
    }

    /**
     * The path <code>server/$engine</code>
     *
     * @return the path <code>server/$engine</code>
     */
    String getServerPath() {
        "server/${getName()}"
    }

    /**
     * The path <code>test/$engine</code>
     *
     * @return the path <code>test/$engine</code>
     */
    String getPath() {
        "${testSuite.path}/${getName()}"
    }

    String toString() {
        getName()
    }

    protected void createFolderAndCopyFilesToTarget(Process process) {
        Engine engine = this

        // engine independent package steps
        ant.mkdir dir: process.targetPath

        ant.echo message: "Copying files for ${process} for engine ${engine}"

        ant.copy file: process.bpelFilePath, todir: process.targetBpelPath
        ant.replace(file: process.targetBpelFilePath, token: "../", value: "")

        process.wsdlPaths.each { wsdlPath ->
            ant.copy file: wsdlPath, todir: process.targetBpelPath
        }

        process.additionalFilePaths.each {additionalPath ->
            ant.copy file: additionalPath, todir: process.targetBpelPath
        }
    }

    protected void bpelFolderToZipFile(Process process) {
        ant.mkdir dir: process.targetPackagePath
        ant.zip file: process.targetPackageFilePath, basedir: process.targetBpelPath
    }

    protected void replaceEndpointAndPartnerTokensWithValues(Process process) {
        ant.echo message: "Setting Endpoint of wsdl IF for $process on ${this} to ${process.endpoint}"
        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "ENDPOINT_URL", value: process.endpoint)
        ant.echo message: "Setting Partner Address of for $process on ${this} to ${Configuration.PARTNER_IP_AND_PORT}"
        ant.replace(dir: process.targetBpelPath, token: "PARTNER_IP_AND_PORT", value: Configuration.PARTNER_IP_AND_PORT)
    }

    public void onPostDeployment() {
        // do nothing, can be overridden
    }

    public void onPostDeployment(Process process) {

    }

    void prepare() {
        ant.mkdir dir: path
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Engine engine = (Engine) o

        if (getName() != engine.getName()) return false

        return true
    }

    int hashCode() {
        return (getName() != null ? getName().hashCode() : 0)
    }

}