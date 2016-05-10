package betsy.common.virtual;

import betsy.common.virtual.docker.Container;
import betsy.common.virtual.docker.DockerMachine;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class executes the containers parallel for the given constraints and workerTemplates.
 *
 */
public class Spawner {

    private static final Logger LOGGER = Logger.getLogger(ParallelRunner.class);

    private ArrayList<WorkerTemplate> workerTemplates;
    private DockerMachine dockerMachine;
    private ExecutorService executor;
    private int cpuShares;
    private int memory;
    private int hddSpeed;

    /**
     *
     * @param dockerMachine The dockermachine to execute on.
     * @param workerTemplates The workerTemplates to execute.
     * @param number The number of parallel executed containers.
     * @param cpuShares The cpuShares for a single container.
     * @param memory The maximum memory usage of container.
     * @param hddSpeed The maximum hddSpeed of a single container.
     */
    public Spawner(DockerMachine dockerMachine, ArrayList<WorkerTemplate> workerTemplates, int number, int cpuShares, int memory, int hddSpeed) {
        this.dockerMachine = dockerMachine;
        this.workerTemplates = workerTemplates;
        this.cpuShares = cpuShares;
        this.memory = memory;
        this.hddSpeed = hddSpeed;
        executor = Executors.newFixedThreadPool(number);
    }


    /**
     *
     * Starts the parallel execution of the containers.
     *
     * @return Returns a list with the containers.
     */
    public List<Container> start() {
        ArrayList<Future<Container>> futures = new ArrayList<>();
        ArrayList<Container> containers = new ArrayList<>();

        for(WorkerTemplate workerTemplate : workerTemplates){
            Future<Container> container = executor.submit(new Worker(dockerMachine, workerTemplate, cpuShares, memory, hddSpeed));
            futures.add(container);
        }

        for(Future<Container> containerFuture : futures){
            try {
                containers.add(containerFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.info("Can't get the result of the future: " + futures.toString());
            }
        }
        executor.shutdown();
        return containers;
    }
}
