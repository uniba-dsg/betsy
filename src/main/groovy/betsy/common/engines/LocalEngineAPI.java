package betsy.common.engines;

import java.nio.file.Path;

public interface LocalEngineAPI {
    /**
     * The path <code>server/$engine</code>
     *
     * @return the path <code>server/$engine</code>
     */
    Path getServerPath();
}
