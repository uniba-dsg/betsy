package betsy.bpel.engines.openesb

import ant.tasks.AntUtil
import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks

import java.nio.file.Path
import java.nio.file.Paths

class OpenEsbInstaller {

    final AntBuilder ant = AntUtil.builder()

    Path serverDir = Paths.get("server/openesb")
    String fileName = "glassfishesb-v2.2-full-installer-windows.exe"
    Path stateXmlTemplate = Paths.get(OpenEsbInstaller.class.getResource("/openesb/state.xml.template").toURI())

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        FileTasks.deleteDirectory(serverDir)
        FileTasks.mkdirs(serverDir)

        Path stateXmlPath = serverDir.resolve("state.xml").toAbsolutePath()
        ant.copy file: stateXmlTemplate, tofile: stateXmlPath, {
            filterchain {
                replacetokens {
                    token key: "INSTALL_PATH", value: serverDir.toAbsolutePath()
                    token key: "JDK_LOCATION", value: System.getenv()['JAVA_HOME']
                    token key: "HTTP_PORT", value: 8383
                    token key: "HTTPS_PORT", value: 8384
                }
            }
        }

        Path reinstallGlassFishBatPath = serverDir.resolve("reinstallGlassFish.bat")
        java.nio.file.Files.copy(Paths.get(OpenEsbInstaller.class.getResource("/openesb/reinstallGlassFish.bat").toURI()),
                reinstallGlassFishBatPath)

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(
                reinstallGlassFishBatPath).values(
                Configuration.downloadsDir.resolve(fileName).toString(),
                stateXmlPath.toString())
        )

    }
}
