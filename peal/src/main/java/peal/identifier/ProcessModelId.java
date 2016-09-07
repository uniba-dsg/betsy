package peal.identifier;

import java.util.Objects;

import javax.xml.namespace.QName;

public class ProcessModelId extends EngineId {

    public ProcessModelId(String engineId, QName processId) {
        super(engineId);
        this.processId = processId;
    }

    @Override
    public String toString() {
        return String.join("/", getEngineId(), getProcessId().toString());
    }

    public QName getProcessId() {
        return processId;
    }

    private final QName processId;

    public EngineId toEngineId() {
        return new EngineId(getEngineId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        ProcessModelId that = (ProcessModelId) o;
        return Objects.equals(processId, that.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), processId);
    }
}
