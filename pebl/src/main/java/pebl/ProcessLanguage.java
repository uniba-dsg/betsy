package pebl;

import java.nio.file.Path;

public enum ProcessLanguage {
    BPEL, BPMN, UNKNOWN;

    public String getID() {
        return name();
    }
}
