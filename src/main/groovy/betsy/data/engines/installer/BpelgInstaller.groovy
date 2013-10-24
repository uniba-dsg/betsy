package betsy.data.engines.installer

import static betsy.data.engines.installer.Constants.DOWNLOADS_DIR

class BpelgInstaller {

    AntBuilder ant = new AntBuilder()

    String serverDir = "server/bpelg"

    String fileName = "bpel-g-5.3.war"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"

    String databaseName = "h2-1.2.122.jar"
    String databaseDownloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${databaseName}"

    public void install() {
        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir,
                additionalVmParam: "-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl")
        tomcatInstaller.install()

        ant.get(dest: DOWNLOADS_DIR, skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.unzip src: "${DOWNLOADS_DIR}/${fileName}", dest: "${serverDir}/${tomcatInstaller.tomcatName}/webapps/bpel-g"
        ant.copy file: "src/main/resources/bpelg/log4j.properties", todir: "${serverDir}/${tomcatInstaller.tomcatName}/webapps/bpel-g/WEB-INF", overwrite: true

        ant.get(dest: DOWNLOADS_DIR, skipexisting: true) {
            ant.url url: databaseDownloadUrl
        }
        ant.copy file: "${DOWNLOADS_DIR}/${databaseName}", tofile: "${serverDir}/${tomcatInstaller.tomcatName}/lib/${databaseName}"
    }

}
