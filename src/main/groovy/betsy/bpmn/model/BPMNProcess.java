package betsy.bpmn.model;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.model.AbstractProcess;
import betsy.common.model.Group;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class BPMNProcess extends AbstractProcess<BPMNTestCase, AbstractBPMNEngine> {

    public BPMNProcess() {

    }

    public BPMNProcess(Path process, String description, List<BPMNTestCase> testCases, Group group) {
        this.setProcess(process);
        this.setDescription(description);
        this.setTestCases(new LinkedList<>(testCases));
        this.setGroup(group);
    }

    public BPMNProcess createCopyWithoutEngine() {
        return new BPMNProcess(getProcess(), getDescription(), getTestCases(), getGroupObject());
    }

    public String getGroupId() {
        return "de.uniba.dsg";
    }

    public String getVersion() {
        return "1.0";
    }

    public Path getTargetReportsPathWithCase(int testCaseNumber) {
        return getTargetReportsPath().resolve("case" + testCaseNumber);
    }

    public Path getTargetTestBinPath() {
        return getTargetPath().resolve("testBin");
    }

    public Path getTargetTestBinPathWithCase(int testCaseName) {
        return getTargetTestBinPath().resolve("case" + testCaseName);
    }

    public Path getTargetTestSrcPath() {
        return getTargetPath().resolve("testSrc");
    }

    public Path getTargetTestSrcPathWithCase(int testCaseNumber) {
        return getTargetTestSrcPath().resolve("case" + testCaseNumber);
    }

}
