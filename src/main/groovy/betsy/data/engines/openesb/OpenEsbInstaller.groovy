package betsy.data.engines.openesb

import ant.tasks.AntUtil
import betsy.Configuration

class OpenEsbInstaller {

    final AntBuilder ant = AntUtil.builder()

    String serverDir = "server/openesb"
    String fileName = "glassfishesb-v2.2-full-installer-windows.exe"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"
    String stateXmlTemplate = "src/main/resources/openesb/state.xml.template"

    public void install() {
        //TODO adpot for OpnEsb23

        ant.get(dest: Configuration.get("downloads.dir"), skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.delete dir: serverDir
        ant.mkdir dir: serverDir

        ant.copy file: stateXmlTemplate, tofile: "${serverDir}/state.xml", {
            filterchain {
                replacetokens {
                    token key: "INSTALL_PATH", value: new File(serverDir).absolutePath
                    token key: "JDK_LOCATION", value: System.getenv()['JAVA_HOME']
                    token key: "HTTP_PORT", value: 8383
                    token key: "HTTPS_PORT", value: 8384
                }
            }
        }

        ant.exec executable: "cmd", {
            arg value: "/c"
            arg value: new File("src/main/resources/openesb/reinstallGlassFish.bat").absolutePath
            arg value: new File("${Configuration.get("downloads.dir")}/${fileName}").absolutePath
            arg value: new File("${serverDir}/state.xml").absolutePath
        }
    }
}
