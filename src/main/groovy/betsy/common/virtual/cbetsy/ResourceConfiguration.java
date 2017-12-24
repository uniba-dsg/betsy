package betsy.common.virtual.cbetsy;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          This class represents the resources of a system or of a container.
 */
public class ResourceConfiguration {

    private final int memory;
    private final int hddSpeed;

    /**
     *
     * @param memory The memory of the system or the container.
     * @param hddSpeed The hddSpeed of the system or the container.
     */
    public ResourceConfiguration(int memory, int hddSpeed) {
        this.memory = memory;
        this.hddSpeed = hddSpeed;
    }

    /**
     *
     * @return Returns the memory of the configuration.
     */
    public int getMemory() {
        return memory;
    }

    /**
     *
     * @return Returns the hddSpeed of the engine.
     */
    public int getHddSpeed() {
        return hddSpeed;
    }


}
