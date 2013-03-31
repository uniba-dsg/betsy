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

	private List<DataContainer> engineLogfiles = new LinkedList<>();
	private List<DataContainer> betsyLogfiles = new LinkedList<>();

	public void addBetsyLogfile(DataContainer logfile) {
		this.betsyLogfiles.add(logfile);
	}

	public void addBetsyLogfiles(List<DataContainer> logfileCollection) {
		for (DataContainer logfile : logfileCollection) {
			this.betsyLogfiles.add(logfile);
		}
	}

	public List<DataContainer> getBetsyLogfiles() {
		return this.betsyLogfiles;
	}

	public void addEngineLogfile(DataContainer logfile) {
		this.engineLogfiles.add(logfile);
	}

	public void addEngineLogfiles(List<DataContainer> logfileCollection) {
		for (DataContainer logfile : logfileCollection) {
			this.engineLogfiles.add(logfile);
		}
	}

	public List<DataContainer> getEngineLogfiles() {
		return this.engineLogfiles;
	}

}
