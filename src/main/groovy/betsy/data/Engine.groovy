package betsy.data

import betsy.data.engines.*

abstract class Engine {

    /**
     * Returns a list of all available engines.
     *
     * @return a list of all available engines
     */
    public static List<Engine> availableEngines() {
        [new OdeEngine(), new BpelgEngine(), new OpenEsbEngine(), new PetalsEsbEngine(), new OrchestraEngine()]
    }

    /**
     * Find an engine by name.
     *
     * @param name the name of an engine
     * @return the engine if it can be found
     * @throws IllegalArgumentException if the engine can not be found
     */
    public static Engine build(String name) {
        Engine engine = availableEngines().find {it.name == name}

        if (engine == null) {
            throw new IllegalArgumentException("passed engine $name does not exist")
        } else {
            return engine
        }
    }

    public static List<Engine> build(List<String> names) {
        names.collect { Engine.build(it) }
    }

    AntBuilder ant = new AntBuilder()

    public abstract String getName()
    public abstract String getDeploymentPrefix()
    public abstract String getDeploymentPostfix()

    TestSuite testSuite

    final List<Process> processes = []

    public void buildAdditionalArchives(Process process) {
        // do nothing
    }

    void setAnt(AntBuilder ant) {
        this.ant = ant
    }

    /**
     * The path <code>src/main/xslt/$engine</code>
     *
     * @return the path <code>src/main/xslt/$engine</code>
     */
    String getXsltPath() {
        "src/main/xslt/${getName()}"
    }

    /**
     * The path <code>server/$engine</code>
     *
     * @return the path <code>server/$engine</code>
     */
    String getServerPath() {
        "server/${getName()}"
    }

    /**
     * The path <code>test/$engine</code>
     *
     * @return the path <code>test/$engine</code>
     */
    String getPath() {
        "${testSuite.path}/${getName()}"
    }

    String toString() {
        getName()
    }

    /**
     * Start the engine and wait until it started.
     */
    public abstract void startup()

    /**
     * Stop the engine immediatly.
     */
    public abstract void shutdown()

    public abstract void install()

    /**
     * Deploy the given <code>process</code> to the current engine.
     *
     * @param process to be deployed
     */
    public abstract void deploy(Process process)

    public void buildDeploymentDescriptor(Process process) {
        // do nothing, can be overridden
    }

    public void onPostDeployment() {
        // do nothing, can be overridden
    }

    public void onPostDeployment(Process process) {

    }

    public void transform(Process process) {
        // do nothing, can be overridden
    }

    public abstract void failIfRunning()

    void prepare() {
        ant.mkdir dir: path
    }

    void storeLogs(Process process) {}

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