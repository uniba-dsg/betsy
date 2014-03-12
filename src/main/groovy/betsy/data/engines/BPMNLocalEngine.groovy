package betsy.data.engines

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran
 * Date: 12.03.14
 * Time: 11:21
 */
public abstract class BPMNLocalEngine extends BPMNEngine implements LocalEngineAPI{
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
