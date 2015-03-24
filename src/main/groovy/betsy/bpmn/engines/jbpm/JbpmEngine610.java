package betsy.bpmn.engines.jbpm;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class JbpmEngine610 extends JbpmEngine {

    @Override
    public String getName() {
        return "jbpm610";
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/" + super.getName());
    }

    @Override
    public String getJbossName() {
        return "wildfly-8.1.0.Final";
    }

    @Override
    public void install() {
        JbpmInstaller610 jbpmInstaller = new JbpmInstaller610();
        jbpmInstaller.setDestinationDir(getServerPath());
        jbpmInstaller.setFileName("jbpm-6.1.0.Final-installer-full.zip");
        jbpmInstaller.install();

    }

    @Override
    public void startup() {
        Path pathToJava7 = Configuration.getJava7Home();
        FileTasks.assertDirectory(pathToJava7);

        Map<String, String> map = new LinkedHashMap<>(1);
        map.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath().resolve("jbpm-installer"), getAntPath().toAbsolutePath() + "/ant -q start.demo.noeclipse"), map);
        Map<String, String> map1 = new LinkedHashMap<>(1);
        map1.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath().resolve("jbpm-installer"), getAntPath().toAbsolutePath() + "/ant -q start.demo.noeclipse"), map1);
        //waiting for jbpm-console for deployment and instantiating
        WaitTasks.waitForSubstringInFile(180000, 5000, getJbossLogDir().resolve("server.log"), "JBAS018559: Deployed \"jbpm-console.war\"");
    }

    private Path getJbossLogDir() {
        return getJbossStandaloneDir().resolve("log");
    }

    public Path getJbossStandaloneDir() {
        return getServerPath().resolve("jbpm-installer").resolve(getJbossName()).resolve("standalone");


//    @Override
//    public Path getServerPath() {
//        return Paths.get("server").resolve(getName().concat("\\jbpm-installer"));
//    }


    }
}