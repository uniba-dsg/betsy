package pebl;

import java.util.Objects;

import javax.xml.bind.annotation.XmlValue;

public class ProcessLanguage implements HasID,Comparable<ProcessLanguage> {
    public static final ProcessLanguage BPEL = new ProcessLanguage("BPEL", "2.0");
    public static final ProcessLanguage BPMN = new ProcessLanguage("BPMN", "2.0");
    public static final ProcessLanguage UNKNOWN = new ProcessLanguage("NAME", "VERSION");

    @XmlValue
    private final String id;

    ProcessLanguage() {
        this("", "");
    }

    public ProcessLanguage(String name, String version) {
        this.id = String.join(HasID.SEPARATOR, Objects.requireNonNull(name), Objects.requireNonNull(version));
    }

    public String getID() {
        return id;
    }

    @Override
    public int compareTo(ProcessLanguage o) {
        return getID().compareTo(o.getID());
    }
}
