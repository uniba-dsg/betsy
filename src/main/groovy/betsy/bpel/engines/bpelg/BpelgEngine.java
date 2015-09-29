package betsy.bpel.engines.bpelg;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.engines.ProcessLanguage;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.model.Engine;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class BpelgEngine extends AbstractLocalBPELEngine {

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/bpelg");
    }

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPEL, "bpelg", "5.3");
    }

    public Path getDeploymentDir() {
        return getTomcat().getTomcatDir().resolve("bpr");
    }

    @Override
    public void storeLogs(BPELProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());

        for (Path p : getLogs()) {
            FileTasks.copyFileIntoFolder(p, process.getTargetLogsPath());
        }
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getTomcat().getTomcatLogsDir()));

        return result;
    }

    @Override
    public boolean isRunning() {
        return getTomcat().checkIfIsRunning();
    }

    @Override
    public String getEndpointUrl(final BPELProcess process) {
        return getTomcat().getTomcatUrl() + "/bpel-g/services/" + process.getName() + "TestInterfaceService";
    }

    public Tomcat getTomcat() {
        return Tomcat.v7(getServerPath());
    }

    @Override
    public void startup() {
        getTomcat().startup();
    }

    @Override
    public void shutdown() {
        getTomcat().shutdown();
    }

    @Override
    public void install() {
        new BpelgInstaller(getServerPath(), "bpel-g-5.3.war").install();
    }

    @Override
    public void deploy(BPELProcess process) {
        new BpelgDeployer(getDeploymentDir(), getTomcat().getTomcatLogsDir().resolve("bpelg.log")).deploy(process.getTargetPackageFilePath(), process.getName());
    }

    @Override
    public void buildArchives(final BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // deployment descriptor
        XSLTTasks.transform(getXsltPath().resolve("bpelg_bpel_to_deploy_xml.xsl"), process.getProcess(), process.getTargetProcessPath().resolve("deploy.xml"));

        // remove unimplemented methods
        for (String pattern : Util.computeMatchingPattern(process)) {
            FileTasks.copyFileContentsToNewFile(process.getTargetProcessPath().resolve("TestInterface.wsdl"),
                    process.getTargetTmpPath().resolve("TestInterface.wsdl.before_removing_" + pattern));

            XSLTTasks.transform(getXsltPath().resolve("bpelg_prepare_wsdl.xsl"),
                    process.getTargetTmpPath().resolve("TestInterface.wsdl.before_removing_" + pattern),
                    process.getTargetTmpPath().resolve("TestInterface.wsdl.after_removing_" + pattern),
                    "deletePattern", pattern);

            FileTasks.copyFileIntoFolderAndOverwrite(process.getTargetTmpPath().resolve("TestInterface.wsdl.after_removing_" + pattern),
                    process.getTargetProcessPath());
        }

        // uniquify service name
        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("TestInterface.wsdl"), "TestInterfaceService", process.getName() + "TestInterfaceService");
        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("deploy.xml"), "TestInterfaceService", process.getName() + "TestInterfaceService");

        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);
        getPackageBuilder().bpelFolderToZipFile(process);
    }

}
