package betsy.virtual.common.messages;

/**
 * {@link StatusMessage}s are exchanged between the client and the server. They
 * can be used as a request or as an answer.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public enum StatusMessage {

	// Ask for availability
	PING("PING"),
	// Confirmation of availability
	PONG("PONG"),
	// confirm last action/request
	OK("OK"),
	// check logs why
	DEPLOY_FAILED("DEPLOY_FAILED"),
	// essential to know when start testing
	DEPLOYED("DEPLOYED"),
	// exit connection
	EXIT("EXIT"),
	// answer to logfile request
	NO_LOGFILE_AVAILABLE("No logfile was available to be sent."),
	// if the request is not known. indicating incompatible versions.
	ERROR_INVALID_REQUEST("Received request is unknown, incompatible versions?"),
	ERROR_CHECKSUM("Checksum of received files did not match."),
	ERROR_INCOMPATIBLE_ENGINE("Incompatible engine, is not virtualized yet."),
	ERROR_ENGINE_EXPECTED("Server expected to receive the engine's type."),
	ERROR_COLLECT_LOGFILES("Collecting logfiles failed.");

	private final String statusName;

	StatusMessage(final String message) {
		this.statusName = message;
	}

	@Override
	public String toString() {
		return this.statusName;
	}

}
