package betsy.bpel.engines.orchestra;

import betsy.bpel.engines.AbstractLocalEngine;
import betsy.bpel.engines.tomcat.Tomcat;
import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import betsy.common.tasks.FileTasks;

public class OrchestraEngine extends AbstractLocalEngine {
    @Override
    public String getName() {
        return "orchestra";
    }

    public Tomcat getTomcat() {
        Tomcat tomcat = new Tomcat();
        tomcat.setEngineDir(getServerPath());
        return tomcat;
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
        OrchestraDeployer deployer = new OrchestraDeployer();
        deployer.setOrchestraHome(getServerPath().resolve("orchestra-cxf-tomcat-4.9.0"));
        deployer.setPackageFilePath(process.getTargetPackageFilePath());
        deployer.setAntBinFolder(Configuration.getAntHome().resolve("bin").toAbsolutePath());
        deployer.deploy();
    }

    public void buildArchives(BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);
        getPackageBuilder().bpelFolderToZipFile(process);
    }

}
