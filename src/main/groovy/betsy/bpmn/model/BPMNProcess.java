package betsy.bpmn.model;

import betsy.bpmn.engines.BPMNEngine;
import betsy.bpmn.engines.activiti.ActivitiEngine;
import betsy.common.engines.Nameable;
import betsy.common.model.BetsyProcess;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BPMNProcess extends BetsyProcess<BPMNTestCase, BPMNEngine> {

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
