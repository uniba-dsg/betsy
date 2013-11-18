package betsy.data.engines;

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

}
