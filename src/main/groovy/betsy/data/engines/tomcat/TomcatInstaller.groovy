package betsy.data.engines.tomcat

import static betsy.Configuration.DOWNLOADS_DIR

class TomcatInstaller {

    AntBuilder ant = new AntBuilder()

    String destinationDir
    String additionalVmParam = ""

    String tomcatFileName = "apache-tomcat-7.0.26-windows-x64.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/apache-tomcat-7.0.26-windows-x64.zip"
    String tomcatName = "apache-tomcat-7.0.26"

    public void install() {
        ant.mkdir dir: DOWNLOADS_DIR
        ant.get dest: DOWNLOADS_DIR, skipexisting: true, {
            ant.url url: downloadUrl
        }
        ant.delete dir: destinationDir
        ant.mkdir dir: destinationDir
        ant.unzip src: "${DOWNLOADS_DIR}/${tomcatFileName}", dest: destinationDir

        ant.echo file: "${destinationDir}/tomcat_startup.bat", message: getStartupScript()
        ant.echo file: "${destinationDir}/tomcat_shutdown.bat", message: getShutdownScript()
    }

    public String getTomcatDestinationDir() {
        "${destinationDir}/${tomcatName}"
    }

    public String getStartupScript() {
        """SET "CATALINA_OPTS=-Xmx3048M -XX:MaxPermSize=2048m ${additionalVmParam}
cd ${tomcatName}/bin && call startup.bat"""
    }

    public String getShutdownScript() {
        "cd ${tomcatName}/bin && call shutdown.bat"
    }

}
