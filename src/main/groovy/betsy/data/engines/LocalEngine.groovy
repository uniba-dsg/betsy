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

	@Override
	public boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false

		LocalEngine engine = (LocalEngine) o

		if (getName() != engine.getName()) return false

		return true
	}

	@Override
	public int hashCode() {
		return (getName() != null ? getName().hashCode() : 0)
	}
}