package de.uniba.wiai.dsg.betsy.virtual.common.messages;

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
	//
	ERROR_INVALID_REQUEST("Received request is unknown."),
	//
	ERROR_CHECKSUM("Checksum of received files did not match."),
	//
	ERROR_INCOMPATIBLE_ENGINE("Incompatible engine, is not virtualized yet."),
	//
	ERROR_ENGINE_EXPECTED("Server expected to receive the engine's type."),
	//
	ERROR_COLLECT_LOGFILES("Collecting logfiles failed."),

	NO_LOGFILE_AVAILABLE("No logfile was available to be sent.");

	private final String statusName;

	StatusMessage(final String s) {
		this.statusName = s;
	}

	@Override
	public String toString() {
		return this.statusName;
	}

}
