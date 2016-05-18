package betsy.common.virtual.cbetsy;

import java.util.Objects;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          This class represents an engine.
 */
public class DockerEngine {

    private final String name;
    private final TypeOfEngine typeOfEngine;
    private int memory = 0;
    private long time = 0;

    /**
     * @param name The name of the engine.
     * @param typeOfEngine The type of the engine.
     */
    public DockerEngine(String name, TypeOfEngine typeOfEngine) {
        this.name = Objects.requireNonNull(name, "The name can't be null.");
        this.typeOfEngine = Objects.requireNonNull(typeOfEngine, "The type can't be null.");
    }

    /**
     *
     * @return Returns the name of the engine.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Returns the {@link TypeOfEngine} of the engine.
     */
    public TypeOfEngine getTypeOfEngine() {
        return typeOfEngine;
    }

    /**
     *
     * @return Returns the maximum memory usage of the engine.
     */
    public int getMemory() {
        return memory;
    }

    /**
     *
     * @param memory The maximum memory usage of the engine.
     */
    public void setMemory(int memory) {
        this.memory = memory;
    }

    /**
     *
     * @return Returns the duration for executing a process on the engine.
     */
    public long getTime() {
        return time;
    }

    /**
     *
     * @param time The duration for executing a process on the engine.
     */
    public void setTime(long time) {
        this.time = time;
    }


    /**
     * The {@link DockerEngine.TypeOfEngine} of the {@link DockerEngine}.
     */
    public enum TypeOfEngine {
        BPEL, BPMN
    }
}
