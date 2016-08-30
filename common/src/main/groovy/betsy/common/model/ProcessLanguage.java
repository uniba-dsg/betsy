package betsy.common.model;

import java.nio.file.Path;

import betsy.common.util.FileTypes;

public enum ProcessLanguage implements HasID {
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

    public String getID() {
        return name();
    }
}
