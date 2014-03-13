package betsy.data.engines.jbpm

import betsy.data.BPMNProcess
import betsy.data.engines.BPMNEngine
import betsy.executables.BPMNTestBuilder
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks
import betsy.tasks.WaitTasks

import java.nio.file.Path
import java.nio.file.Paths

class JbpmEngine extends BPMNEngine {
    @Override
    String getName() {
        "jbpm"
    }

    String getJbpmnUrl(){
        "http://localhost:8080"
    }

    String getSystemURL(){
        "ssh://admin@localhost:8001/system"
    }

    String getJbossName(){
        "jboss-as-7.1.1.Final"
    }

    Path getJbossStandaloneDir(){
        serverPath.resolve(jbossName).resolve("standalone")
    }

    @Override
    void deploy(BPMNProcess process) {
        // maven deployment for pushing it to the local maven repository (jbpm-console will fetch it from there)
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(Paths.get("D:\\programming\\testo"), "..\\apache-maven-3.2.1\\bin\\mvn clean install"))
        Thread.sleep(1500)
        //preparing ssh
        String homeDir = System.getenv("HOME") //System.getProperty("user.home")
        // delete known_hosts file for do not getting trouble with changing remote finger print
        //FileTasks.deleteFile(Paths.get(homeDir + "/.ssh/known_hosts"))
        FileTasks.createFile(Paths.get(homeDir + "/.ssh/config"), """Host localhost
    StrictHostKeyChecking no""")

        //deploy by creating a deployment unit, which can be started
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(Paths.get("downloads"), "java -jar Jbpm-deployer-1.1.jar ${process.groupId} ${process.name} ${process.version} ${systemURL}"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(Paths.get("downloads"), "java -jar Jbpm-deployer-1.1.jar ${process.groupId} ${process.name} ${process.version} ${systemURL}"))

        //waiting for the result of the deployment
        WaitTasks.sleep(3000)
    }

    @Override
    void buildArchives(BPMNProcess process) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getEndpointUrl(BPMNProcess process) {
        "http://localhost:8080/jbpm-console/"
    }

    @Override
    void storeLogs(BPMNProcess process) {
        FileTasks.mkdirs(process.targetLogsPath)
        ant.copy(todir: process.targetLogsPath) {
            ant.fileset(dir: jbossStandaloneDir.resolve("log"))
            ant.fileset(file: serverPath.resolve("log.txt"))
        }
    }

    @Override
    void install() {
        new JbpmInstaller(destinationDir: serverPath).install()
    }

    @Override
    void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant start.demo.noeclipse"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant start.demo.noeclipse"))

        //waiting for jboss to startup
        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: jbpmnUrl
        }

        //waiting for jbpm-console for deployment and instantiating
        WaitTasks.sleep(120000)
    }

    @Override
    void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant stop.demo"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant stop.demo"))
        //waiting for shutdown
        WaitTasks.sleep(5000)
        // clean up data (with db and config files in the users home directory)
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant clean.demo"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant clean.demo"))
    }

    @Override
    boolean isRunning() {
        try{
            ant.fail(message: "JBoss for engine ${serverPath} is still running") {
                condition() {
                    http url: jbpmnUrl
                }
            }
            return false
        } catch (Exception ignore) {
            return true
        }
    }

    void testProcess(BPMNProcess process){
        new JbpmTester(name: process.name,
                deploymentId: "${process.groupId}:${process.name}:${process.version}",
                baseUrl: new URL(getEndpointUrl(process)),
                testSrc: process.targetTestSrcPath,
                reportPath: process.targetReportsPath,
                testBin: process.targetTestBinPath
        ).runTest()
    }

    void buildTest(BPMNProcess process){
        List<String> assertionList = new ArrayList<String>()

        String line;
        String rpath = process.resourcePath.resolve("assertions.txt").toString()
        BufferedReader br = new BufferedReader(new FileReader(rpath))
        try{
            while ((line = br.readLine()) != null){
                assertionList.add(line);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        new BPMNTestBuilder(packageString: "${name}.${process.group}",
                name: process.name,
                logFile: serverPath.resolve("log.txt"),
                unitTestDir: process.targetTestSrcPath,
                assertionList: assertionList
        ).buildTest()
    }
}
