package betsy.bpel.engines.openesb;

import betsy.common.tasks.FileTasks;

import java.nio.file.Path;
import java.util.Objects;

public class OpenEsbDeployer {
    private final OpenEsbCLI cli;

    public OpenEsbDeployer(OpenEsbCLI cli) {
        this.cli = Objects.requireNonNull(cli);
    }

    public void deploy(String processName, Path packageFilePath, Path tmpFolder) {
        FileTasks.assertFile(packageFilePath);
        FileTasks.assertDirectory(tmpFolder);
        Objects.requireNonNull(processName);

        // create tmp folder
        cli.forceRedeploy(processName, packageFilePath, tmpFolder);
    }

}
