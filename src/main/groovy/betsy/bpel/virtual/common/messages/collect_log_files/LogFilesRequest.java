package betsy.bpel.virtual.common.messages.collect_log_files;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link LogFilesRequest} tells the server to collect all the Logfiles on the
 * given location and return them to the client.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class LogFilesRequest implements Serializable {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private final List<String> paths = new LinkedList<>();

    public List<String> getPaths() {
        return paths;
    }

    @Override
    public String toString() {
        return "LogFilesRequest{" +
                "paths=" + paths +
                '}';
    }
}
