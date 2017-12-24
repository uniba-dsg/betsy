package betsy.common.virtual.cbetsy;

import betsy.common.virtual.docker.Container;
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

    private static final Logger LOGGER = Logger.getLogger(Spawner.class);

    private List<WorkerTemplate> workerTemplates;
    private ExecutorService executor;
    private ResourceConfiguration resourceConfiguration;

    /**
     *
     * @param workerTemplates The workerTemplates to execute.
     * @param resourceConfiguration The configurations for memory, hdd and cpu for this worker.
     * @param number The number of parallel executed containers.
     */
    public Spawner(List<WorkerTemplate> workerTemplates, ResourceConfiguration resourceConfiguration, int number) {
        this.workerTemplates = workerTemplates;
        this.resourceConfiguration = resourceConfiguration;
        this.executor = Executors.newFixedThreadPool(number);
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
            Future<Container> container = executor.submit(new Worker(workerTemplate, resourceConfiguration.getMemory(), resourceConfiguration.getHddSpeed()));
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
