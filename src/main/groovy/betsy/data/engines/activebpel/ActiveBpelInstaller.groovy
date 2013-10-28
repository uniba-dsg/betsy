package betsy.data.engines.activebpel

import betsy.Configuration
import betsy.data.engines.tomcat.TomcatInstaller

class ActiveBpelInstaller {

    AntBuilder ant = new AntBuilder()

    String serverDir = "server/active-bpel"
    String fileName = "activebpel-5.0.2-bin.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"

    public void install() {
        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir,
                additionalVmParam: "-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl",
                tomcatFileName: "apache-tomcat-5.5.36.zip", tomcatName: "apache-tomcat-5.5.36",
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/apache-tomcat-5.5.36.zip")
        tomcatInstaller.install()

        ant.get(dest: Configuration.DOWNLOADS_DIR, skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.unzip src: "${Configuration.DOWNLOADS_DIR}/${fileName}", dest: serverDir

        ant.exec(executable: "cmd", dir: "${serverDir}//activebpel-5.0.2/") {
            arg value: "/c install.bat"
            env key: "CATALINA_HOME", path: tomcatInstaller.tomcatDestinationDir
        }
    }

}
