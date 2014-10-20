package betsy.bpel.engines;

import betsy.common.engines.LocalEngineAPI;
import betsy.common.tasks.FileTasks;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class LocalEngine extends Engine implements LocalEngineAPI {
    /**
     * The path <code>server/$engine</code>
     *
     * @return the path <code>server/$engine</code>
     */
    @Override
    public Path getServerPath() {
        return Paths.get("server").resolve(getName());
    }

    @Override
    public boolean isInstalled() {
        return FileTasks.hasFolder(getServerPath());
    }

    @Override
    public void uninstall() {
        FileTasks.deleteDirectory(getServerPath());
    }

}
