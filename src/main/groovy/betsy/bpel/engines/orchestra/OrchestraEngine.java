package betsy.bpel.engines.orchestra;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.tasks.FileTasks;

public class OrchestraEngine extends AbstractLocalBPELEngine {
    @Override
    public String getName() {
        return "orchestra";
    }

    public Tomcat getTomcat() {
        return Tomcat.v7(getServerPath());
    }

    @Override
    public void install() {
        new OrchestraInstaller().install();
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
        FileTasks.copyFilesInFolderIntoOtherFolder(getTomcat().getTomcatLogsDir(), process.getTargetLogsPath());
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
