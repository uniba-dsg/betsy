package betsy.data

abstract class Engine implements EngineAPI {

    AntBuilder ant = new AntBuilder()
	EnginePackageBuilder packageBuilder = new EnginePackageBuilder(getName())

    TestSuite testSuite

    final List<Process> processes = []

    /**
     * The path <code>src/main/xslt/$engine</code>
     *
     * @return the path <code>src/main/xslt/$engine</code>
     */
    String getXsltPath() {
        "src/main/xslt/${getName()}"
    }

    /**
     * The path <code>test/$engine</code>
     *
     * @return the path <code>test/$engine</code>
     */
    String getPath() {
        "${testSuite.path}/${getName()}"
    }

	@Override
    String toString() {
        getName()
    }

	@Override
    void prepare() {
        // setup engine folder
        ant.mkdir dir: path
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Engine engine = (Engine) o

        if (getName() != engine.getName()) return false

        return true
    }

    int hashCode() {
        return (getName() != null ? getName().hashCode() : 0)
    }
	
}