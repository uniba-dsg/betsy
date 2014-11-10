package betsy.bpmn.model;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.model.AbstractProcess;

import java.nio.file.Path;

public class BPMNProcess extends AbstractProcess<BPMNTestCase, AbstractBPMNEngine> {

    public BPMNProcess createCopyWithoutEngine() {
        BPMNProcess process = new BPMNProcess();
        process.setProcess(getProcess());
        process.setTestCases(getTestCases());
        process.setDescription(getDescription());

        return process;
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
