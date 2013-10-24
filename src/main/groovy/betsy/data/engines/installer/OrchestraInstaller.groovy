package betsy.data.engines.installer

import static betsy.data.engines.installer.Constants.DOWNLOADS_DIR

class OrchestraInstaller {

    AntBuilder ant = new AntBuilder()

    String serverDir = "server/orchestra"
    String fileName = "orchestra-cxf-tomcat-4.9.0.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"
    String installDir = "${serverDir}/orchestra-cxf-tomcat-4.9.0"

    public void install() {
        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir)
        tomcatInstaller.install()

        ant.get(dest: DOWNLOADS_DIR, skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.unzip src: "${DOWNLOADS_DIR}/${fileName}", dest: serverDir

        ant.propertyfile(file: "${installDir}/conf/install.properties") {
            entry key: "catalina.home", value: "../apache-tomcat-7.0.26"
        }

        ant.ant target: "install", dir: installDir
    }

}
