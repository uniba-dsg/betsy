package betsy.common.virtual;

import betsy.common.tasks.FileTasks;
import betsy.common.virtual.docker.Container;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class aggregates the results after an run.
 *
 */
public class Aggregator {

    private List<Container> containers;

    /**
     *
     * @param containers The list with the container to aggregate.
     */
    public Aggregator(List<Container> containers) {
        this.containers = containers;
    }

    /**
     * The method starts the aggregation of the results.
     */
    public void start() {
        Path results = Paths.get("results");
        FileTasks.mkdirs(results);
        for (Container container : containers) {
            Path containerResults = Paths.get(results.toString()+ "/" + container.getName());
            FileTasks.mkdirs(containerResults);
            container.copyFromContainer(new File("/betsy/test"), containerResults.toAbsolutePath());
            container.copyFromContainer(new File("/betsy/betsy.log"), containerResults.toAbsolutePath());
            container.copyFromContainer(new File("/betsy/betsy_time.log"), containerResults.toAbsolutePath());
            container.copyFromContainer(new File("/betsy/betsy_console.log"), containerResults.toAbsolutePath());
        }
    }
}
