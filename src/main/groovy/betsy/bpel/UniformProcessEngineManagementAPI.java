package betsy.bpel;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.engines.EngineAPI;
import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;

import java.nio.file.Path;
import java.util.List;

import javax.xml.namespace.QName;

public class UniformProcessEngineManagementAPI implements EngineAPI<BPELProcess> {

    private final AbstractBPELEngine engine;

    public UniformProcessEngineManagementAPI(AbstractBPELEngine engine) {
        this.engine = engine;
    }

    @Override
    public void deploy(String name, Path path) {
        engine.deploy(name, path);
    }

    @Override
    public boolean isDeployed(QName process) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void undeploy(QName process) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Path buildArchives(BPELProcess process) {
        return engine.buildArchives(process);
    }

    @Override
    public String getEndpointUrl(String name) {
        return engine.getEndpointUrl(name);
    }

    @Override
    public void storeLogs(Path targetLogsPath) {
        engine.storeLogs(targetLogsPath);
    }

    @Override
    public ProcessLanguage getSupportedLanguage() {
        return engine.getSupportedLanguage();
    }

    @Override
    public void install() {
        engine.install();
    }

    @Override
    public void uninstall() {
        engine.uninstall();
    }

    @Override
    public boolean isInstalled() {
        return engine.isInstalled();
    }

    @Override
    public void startup() {
        engine.startup();
    }

    @Override
    public void shutdown() {
        engine.shutdown();
    }

    @Override
    public boolean isRunning() {
        return engine.isRunning();
    }

    @Override
    public List<Path> getLogs() {
        return engine.getLogs();
    }

    @Override public EngineExtended getEngineObject() {
        return engine.getEngineObject();
    }
}
