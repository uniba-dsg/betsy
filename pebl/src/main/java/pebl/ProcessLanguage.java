package pebl;

import java.util.Objects;

import javax.xml.bind.annotation.XmlValue;

public class ProcessLanguage implements HasId,Comparable<ProcessLanguage> {
    public static final ProcessLanguage BPEL = new ProcessLanguage("BPEL", "2.0");
    public static final ProcessLanguage BPMN = new ProcessLanguage("BPMN", "2.0");
    public static final ProcessLanguage UNKNOWN = new ProcessLanguage("NAME", "VERSION");

    @XmlValue
    private final String id;

    ProcessLanguage() {
        this("", "");
    }

    public ProcessLanguage(String name, String version) {
        this.id = String.join(HasId.SEPARATOR, Objects.requireNonNull(name), Objects.requireNonNull(version));
    }

    public String getId() {
        return id;
    }

    @Override
    public int compareTo(ProcessLanguage o) {
        return getId().compareTo(o.getId());
    }
}
