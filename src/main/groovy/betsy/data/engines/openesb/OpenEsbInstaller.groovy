package betsy.data.engines.openesb

import ant.tasks.AntUtil
import betsy.Configuration
import betsy.tasks.ConsoleTasks

import java.nio.file.Path
import java.nio.file.Paths

class OpenEsbInstaller {

    final AntBuilder ant = AntUtil.builder()

    Path serverDir = Paths.get("server/openesb")
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

        ant.copy file: stateXmlTemplate, tofile: serverDir.resolve("state.xml"), {
            filterchain {
                replacetokens {
                    token key: "INSTALL_PATH", value: serverDir.toAbsolutePath()
                    token key: "JDK_LOCATION", value: System.getenv()['JAVA_HOME']
                    token key: "HTTP_PORT", value: 8383
                    token key: "HTTPS_PORT", value: 8384
                }
            }
        }

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(
                Paths.get("src/main/resources/openesb/reinstallGlassFish.bat")).values(
                Configuration.getPath("downloads.dir").resolve(fileName).toString(),
                serverDir.resolve("state.xml").toAbsolutePath().toString())
        )

    }
}
