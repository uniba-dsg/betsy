package betsy.data.engines

abstract class LocalEngine extends Engine implements LocalEngineAPI {

	/**
	 * The path <code>server/$engine</code>
	 *
	 * @return the path <code>server/$engine</code>
	 */
	@Override
	public String getServerPath() {
		"server/${getName()}"
	}

}