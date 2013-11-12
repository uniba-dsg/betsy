package betsy.data.engines

import java.nio.file.Path

interface LocalEngineAPI {

	/**
	 * The path <code>server/$engine</code>
	 *
	 * @return the path <code>server/$engine</code>
	 */
	Path getServerPath()
}
