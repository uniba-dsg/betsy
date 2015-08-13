package betsy.common.engines;

import betsy.common.util.FileTypes;

import java.nio.file.Path;

public enum ProcessLanguage {
    BPEL, BPMN, UNKNOWN;

    public static ProcessLanguage getByPath(Path path) {
        return getByPath(path.toString());
    }

    public static ProcessLanguage getByPath(String path) {
        String lowerCasePath = path.toLowerCase();
        if (lowerCasePath.endsWith(FileTypes.BPMN)) {
            return ProcessLanguage.BPMN;
        } else if (lowerCasePath.endsWith(FileTypes.BPEL)) {
            return ProcessLanguage.BPEL;
        } else {
            return ProcessLanguage.UNKNOWN;
        }
    }
}
