package betsy.common.virtual.swarm.messages;

import java.io.Serializable;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public final class FinishMessage implements Serializable {

    private boolean isFinish;

    public FinishMessage(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public boolean isFinish() {
        return isFinish;
    }
}
