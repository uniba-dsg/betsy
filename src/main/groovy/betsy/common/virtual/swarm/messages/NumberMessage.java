package betsy.common.virtual.swarm.messages;

import java.io.Serializable;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public final class NumberMessage implements Serializable {
    private int number;

    public NumberMessage(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
