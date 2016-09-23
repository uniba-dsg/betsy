package peal.identifier;

import java.util.Objects;

public class EngineId {

    private final String engineId;

    public EngineId(String engineId) {
        this.engineId = engineId;
    }

    public String getEngineId() {
        return engineId;
    }

    @Override
    public String toString() {
        return engineId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EngineId engineId1 = (EngineId) o;
        return Objects.equals(engineId, engineId1.engineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(engineId);
    }
}
