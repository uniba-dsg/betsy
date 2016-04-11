package betsy.bpel;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.engines.EngineAPI;
import betsy.common.model.ProcessLanguage;
import betsy.common.model.Engine;

import java.nio.file.Path;
import java.util.List;

public class UniformProcessEngineManagementAPI implements EngineAPI<BPELProcess> {

    private final AbstractBPELEngine engine;

    public UniformProcessEngineManagementAPI(AbstractBPELEngine engine) {
        this.engine = engine;
    }

    @Override
    public void deploy(BPELProcess process) {
        engine.deploy(process);
    }

    @Override
    public void buildArchives(BPELProcess process) {
        engine.buildArchives(process);
    }

    @Override
    public String getEndpointUrl(BPELProcess process) {
        return engine.getEndpointUrl(process);
    }

    @Override
    public void storeLogs(BPELProcess process) {
        engine.storeLogs(process);
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

    @Override
    public Engine getEngineId() {
        return engine.getEngineId();
    }

}
