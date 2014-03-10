package betsy.data.engines.jbpm

import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks

import java.nio.file.Paths

/**
 * Created with IntelliJ IDEA.
 * User: stavorndran
 * Date: 04.03.14
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
class JbpmEngine extends LocalEngine {
    @Override
    String getName() {
        "jbpm"
    }

    String getJbpmnUrl(){
        "http://localhost:8080"
    }

    @Override
    void deploy(BetsyProcess process) {
        //preparing ssh
        String homeDir = System.getenv("HOME") //System.getProperty("user.home")
        // delete known_hosts file for do not getting trouble with changing remote finger print
        FileTasks.deleteFile(Paths.get(homeDir + "/.ssh/known_hosts"))
        FileTasks.createFile(Paths.get(homeDir + "/.ssh/config"), """Host localhost
    StrictHostKeyChecking no""")

        //deploy
        String groupId = "org.jbpm"
        String artifactId = "Evaluation"
        String version = "1.0"
        String systemUrl = "ssh://admin@localhost:8001/system"
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(Paths.get("downloads"), "java -jar Jbpm-deployer-1.1.jar ${groupId} ${artifactId} ${version} ${systemUrl}"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(Paths.get("downloads"), "java -jar Jbpm-deployer-1.1.jar ${groupId} ${artifactId} ${version} ${systemUrl}"))
    }

    @Override
    void buildArchives(BetsyProcess process) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void storeLogs(BetsyProcess process) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void install() {
        new JbpmInstaller(destinationDir: serverPath).install()
    }

    @Override
    void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant start.demo.noeclipse"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant start.demo.noeclipse"))

        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: jbpmnUrl
        }
    }

    @Override
    void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant stop.demo"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant stop.demo"))
        Thread.sleep(5000)
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant clean.demo"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant clean.demo"))
    }

    @Override
    boolean isRunning() {
        ant.fail(message: "JBoss for engine ${serverPath} is still running") {
            condition() {
                http url: jbpmnUrl
            }
        }
    }
}
