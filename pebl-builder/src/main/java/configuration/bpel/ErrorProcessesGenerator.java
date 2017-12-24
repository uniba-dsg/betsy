package configuration.bpel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import betsy.common.tasks.FileTasks;
import pebl.benchmark.test.Test;

import static configuration.FilesLocation.BPEL_LOCATION;

public class ErrorProcessesGenerator {
    private static List<Test> createProcesses() {
        Path errorsFolder = Paths.get(BPEL_LOCATION).resolve("errors");
        FileTasks.deleteDirectory(errorsFolder);
        FileTasks.mkdirs(errorsFolder);

        List<Test> result = ErrorProcesses.getProcesses();

        for (Test process : result) {
            // update fileName
            String processFileName = process.getName();
            if (processFileName.startsWith("IBR_")) {
                XMLTasks.updatesNameAndNamespaceOfRootElement(ErrorProcesses.TestTemplate.IMPROVED_BACKDOOR_ROBUSTNESS.getBase().getProcess(), process.getProcess(), processFileName);
            } else if (processFileName.startsWith("BR_")) {
                XMLTasks.updatesNameAndNamespaceOfRootElement(ErrorProcesses.TestTemplate.BACKDOOR_ROBUSTNESS.getBase().getProcess(), process.getProcess(), processFileName);
            }
        }

        return result;// make sure the happy path is the first test
    }

    public static void main(String... args) {
        createProcesses(); // this is to recreate the error processes
    }
}
