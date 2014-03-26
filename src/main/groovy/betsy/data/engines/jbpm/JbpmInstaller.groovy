package betsy.data.engines.jbpm

import ant.tasks.AntUtil
import betsy.config.Configuration
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks

import java.nio.file.Path

class JbpmInstaller {
    private static final AntBuilder ant = AntUtil.builder()

    Path destinationDir

    String fileName = "jbpm-installer.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"

    public void install() {
        FileTasks.deleteDirectory(destinationDir)
        FileTasks.mkdirs(destinationDir)

        ant.get(dest: Configuration.get("downloads.dir"), skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.unzip src: Configuration.getPath("downloads.dir").resolve(fileName),
                dest: destinationDir

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir, "ant -q install.demo.noeclipse"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir, "ant -q install.demo.noeclipse"))
    }

    @Override
    public String toString() {
        return "JBPMInstaller{" +
                "destinationDir='" + destinationDir + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
