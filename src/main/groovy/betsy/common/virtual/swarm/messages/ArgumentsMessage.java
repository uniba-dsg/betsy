package betsy.common.virtual.swarm.messages;

import java.io.Serializable;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public final class ArgumentsMessage implements Serializable {

    private String[] args;

    public ArgumentsMessage(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }
}
