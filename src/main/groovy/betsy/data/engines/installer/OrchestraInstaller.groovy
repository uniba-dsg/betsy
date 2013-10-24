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

    /*
        <property name="orchestra.dir" value="server/orchestra"/>
    <property name="orchestra.name" value="orchestra-cxf-tomcat-4.9.0"/>
    <property name="orchestra.file.name" value="orchestra-cxf-tomcat-4.9.0.zip"/>
    <property name="orchestra.download.url" value="${download.base.url}${orchestra.file.name}"/>

    <target name="orchestra" description="Install Orchestra">

        <!-- set properties to custom tomcat installation (not the one included) -->
        <propertyfile file="${orchestra.dir}/${orchestra.name}/conf/install.properties">
            <entry key="catalina.home" value="../apache-tomcat-7.0.26"/>
        </propertyfile>

        <!-- configure orchestra to use custom tomcat installation -->
        <ant target="install" dir="${orchestra.dir}/${orchestra.name}"/>
    </target>
     */
}
