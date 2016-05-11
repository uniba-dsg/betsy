package betsy.common.virtual;

import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.Containers;
import betsy.common.virtual.docker.DockerMachine;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class represents a worker, which starts the container for the given workerTemplate.
 *
 */
public class Worker implements Callable<Container> {

    private WorkerTemplate workerTemplate;
    private DockerMachine dockerMachine;
    private int cpuShares;
    private int memory;
    private int hddSpeed;

    /**
     *
     * @param dockerMachine The dockerMachine to execute on.
     * @param workerTemplate The workerTemplate to execute.
     * @param cpuShares The cpuShares for the container.
     * @param memory The maximum memory of the container.
     * @param hddSpeed The hddSpeed of the container.
     */
    public Worker(DockerMachine dockerMachine, WorkerTemplate workerTemplate, int cpuShares, int memory, int hddSpeed) {
        this.dockerMachine = dockerMachine;
        this.workerTemplate = workerTemplate;
        this.cpuShares = cpuShares;
        this.memory = memory;
        this.hddSpeed = hddSpeed;
    }


    @Override
    public Container call() throws Exception {
        Container container = Containers.create(dockerMachine, workerTemplate.getID(), workerTemplate.getEngineName().replace("_", ""), cpuShares, memory, hddSpeed, workerTemplate.getCmd());
        container.copyToContainer(Paths.get(workerTemplate.getEngineName()+ "/timeout.properties"), Paths.get("/betsy"));
        container.start(false);
        return container;
    }
}
