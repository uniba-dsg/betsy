package betsy.common.util;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LogFileUtil {

    /**
     * Requires BPEL or BPMN Engine to be passed!
     */
    public static Path copyLogsToTempFolder(Object e) {
        final Path tmpFolder = createTempFolder(e.toString());
        if(e instanceof AbstractBPELEngine) {
            AbstractBPELEngine eNew = (AbstractBPELEngine) e;
            eNew.storeLogs(new BPELProcess() {
                @Override
                public Path getTargetLogsPath() {
                    return tmpFolder;
                }
            });
        } else if(e instanceof AbstractBPMNEngine) {
            AbstractBPMNEngine eNew = (AbstractBPMNEngine) e;
            eNew.storeLogs(new BPMNProcess() {
                @Override
                public Path getTargetLogsPath() {
                    return tmpFolder;
                }
            });
        }
        return tmpFolder;
    }

    public static Path createTempFolder(String context) {
        try {
            return Files.createTempDirectory("betsy-" + context + "-logs");
        } catch (IOException e1) {
            throw new IllegalStateException("Could not create temp folder", e1);
        }
    }

}
