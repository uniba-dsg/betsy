package peal.identifier;

import java.util.Objects;

import javax.xml.namespace.QName;

public class InstanceId extends ProcessModelId {

    private final String instanceID;

    public InstanceId(String engineId, QName processId, String instanceID) {
        super(engineId, processId);
        this.instanceID = instanceID;
    }

    @Override
    public String toString() {
        return String.join("/", getEngineId(), getProcessId().toString(), instanceID);
    }

    public String getInstanceID() {
        return instanceID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        InstanceId that = (InstanceId) o;
        return Objects.equals(instanceID, that.instanceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), instanceID);
    }
}
