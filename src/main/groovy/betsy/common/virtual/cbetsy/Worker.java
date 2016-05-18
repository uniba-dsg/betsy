package betsy.common.virtual.cbetsy;

import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.Containers;
import betsy.common.virtual.docker.DockerMachine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class represents a worker, which starts the container for the given workerTemplate.
 *
 */
public class Worker implements Callable<Container> {

    private static Path docker = Paths.get(get("docker.dir")).toAbsolutePath();
    private WorkerTemplate workerTemplate;
    private DockerMachine dockerMachine;
    private int memory;
    private int hddSpeed;

    /**
     *
     * @param dockerMachine The dockerMachine to execute on.
     * @param workerTemplate The workerTemplate to execute.
     * @param memory The maximum memory of the container.
     * @param hddSpeed The hddSpeed of the container.
     */
    public Worker(DockerMachine dockerMachine, WorkerTemplate workerTemplate, int memory, int hddSpeed) {
        this.dockerMachine = dockerMachine;
        this.workerTemplate = workerTemplate;
        this.memory = memory;
        this.hddSpeed = hddSpeed;
    }


    @Override
    public Container call() throws Exception {
        Container container = Containers.create(dockerMachine, workerTemplate.getID(), workerTemplate.getDockerEngine().getName().replace("_", ""), memory, hddSpeed, workerTemplate.getCmd());
        container.copyToContainer(docker.resolve("timeouts").resolve(workerTemplate.getDockerEngine().getName().replace("_", "")).resolve("timeout.properties").toAbsolutePath(), Paths.get("/betsy"));
        container.start(false);
        return container;
    }
}
