package de.uniba.wiai.dsg.betsy.virtual.common.messages;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

//TODO JAVADOC
public class LogfileCollection implements Serializable {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private List<FileMessage> engineLogfiles = new LinkedList<>();
	private List<FileMessage> betsyLogfiles = new LinkedList<>();

	public void addBetsyLogfile(FileMessage logfile) {
		this.betsyLogfiles.add(logfile);
	}

	public void addBetsyLogfiles(List<FileMessage> logfileCollection) {
		for (FileMessage logfile : logfileCollection) {
			this.betsyLogfiles.add(logfile);
		}
	}

	public List<FileMessage> getBetsyLogfiles() {
		return this.betsyLogfiles;
	}

	public void addEngineLogfile(FileMessage logfile) {
		this.engineLogfiles.add(logfile);
	}

	public void addEngineLogfiles(List<FileMessage> logfileCollection) {
		for (FileMessage logfile : logfileCollection) {
			this.engineLogfiles.add(logfile);
		}
	}

	public List<FileMessage> getEngineLogfiles() {
		return this.engineLogfiles;
	}

}
