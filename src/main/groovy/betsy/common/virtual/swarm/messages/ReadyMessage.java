package betsy.common.virtual.swarm.messages;

import java.io.Serializable;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public final class ReadyMessage implements Serializable {

    private boolean isReady;

    public ReadyMessage(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isReady() {
        return isReady;
    }
}
