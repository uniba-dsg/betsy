package betsy.bpel.engines.orchestra;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.engines.ProcessLanguage;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.model.Engine;
import betsy.common.tasks.FileTasks;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class OrchestraEngine extends AbstractLocalBPELEngine {

    public Path getXsltPath() {
        throw new IllegalStateException("unused");
    }

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPEL, "orchestra", "4.9");
    }

    public Tomcat getTomcat() {
        return Tomcat.v7(getServerPath());
    }

    @Override
    public void install() {
        new OrchestraInstaller(getServerPath()).install();
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
    public boolean isRunning() {
        return getTomcat().checkIfIsRunning();
    }

    @Override
    public String getEndpointUrl(final BPELProcess process) {
        return getTomcat().getTomcatUrl() + "/orchestra/" + process.getName() + "TestInterface";
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
    public void deploy(BPELProcess process) {
        OrchestraDeployer deployer = new OrchestraDeployer(getServerPath().resolve("orchestra-cxf-tomcat-4.9.0"));
        deployer.deploy(process.getTargetPackageFilePath(), process.getName());
    }

    public void buildArchives(BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);
        getPackageBuilder().bpelFolderToZipFile(process);
    }

}
