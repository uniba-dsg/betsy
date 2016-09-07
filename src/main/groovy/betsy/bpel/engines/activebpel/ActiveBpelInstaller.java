package betsy.bpel.engines.activebpel;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import betsy.common.config.Configuration;
import betsy.common.engines.tomcat.TomcatInstaller;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

public class ActiveBpelInstaller {

    private final Path serverDir;

    public ActiveBpelInstaller(Path serverDir) {
        this.serverDir = serverDir;
    }

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir);

        TomcatInstaller installer = TomcatInstaller.v5(serverDir);
        installer.setAdditionalVmParam("-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl");
        installer.install();

        String fileName = "activebpel-5.0.2-bin.zip";
        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), serverDir);

        Map<String, String> map = new HashMap<>();
        map.put("CATALINA_HOME", "../" + installer.getTomcat().getTomcatName());

        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(serverDir.resolve("activebpel-5.0.2"), "install.bat"), map);

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("+x", serverDir.resolve("activebpel-5.0.2").resolve("install.sh").toString()));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("sync"));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build(serverDir.resolve("activebpel-5.0.2"), serverDir.resolve("activebpel-5.0.2").resolve("install.sh")), map);
    }
}
