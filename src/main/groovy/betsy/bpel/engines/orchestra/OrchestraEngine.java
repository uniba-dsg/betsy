package betsy.bpel.engines.orchestra;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.engines.tomcat.Tomcat;
import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.FileTasks;

public class OrchestraEngine extends AbstractLocalBPELEngine {

    public Path getXsltPath() {
        throw new IllegalStateException("unused");
    }

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPEL, "orchestra", "4.9", LocalDate.of(2012,1,23), "LGPL-2.1+");
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
    public String getEndpointUrl(String name) {
        return getTomcat().getTomcatUrl() + "/orchestra/" + name + "TestInterface";
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getTomcat().getTomcatLogsDir()));

        return result;
    }

    @Override
    public void deploy(String name, Path path) {
        OrchestraDeployer deployer = new OrchestraDeployer(getServerPath().resolve("orchestra-cxf-tomcat-4.9.0"));
        deployer.deploy(path, name);
    }

    @Override public boolean isDeployed(QName process) {
        OrchestraDeployer deployer = new OrchestraDeployer(getServerPath().resolve("orchestra-cxf-tomcat-4.9.0"));
        return deployer.isDeployed(process.getLocalPart());
    }

    @Override public void undeploy(QName process) {
        OrchestraDeployer deployer = new OrchestraDeployer(getServerPath().resolve("orchestra-cxf-tomcat-4.9.0"));
        deployer.undeploy(process);
    }

    public Path buildArchives(BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);
        getPackageBuilder().bpelFolderToZipFile(process);

        return process.getTargetPackageFilePath();
    }

}
