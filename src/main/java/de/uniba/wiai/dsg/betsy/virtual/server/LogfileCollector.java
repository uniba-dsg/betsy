package de.uniba.wiai.dsg.betsy.virtual.server;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.Checksum;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.CollectLogfileException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.FileMessage;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.LogRequest;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.LogfileCollection;

public class LogfileCollector {

	private Logger log = Logger.getLogger(getClass());

	public LogfileCollection collectLogfiles(final LogRequest request)
			throws CollectLogfileException {

		File betsyServerInstallDir = new File(request.getBetsyInstallDir());
		if (!betsyServerInstallDir.isDirectory()) {
			throw new CollectLogfileException("The dir to collect the bVMS "
					+ "logs from does not exist.");
		}
		File engineLogDirectory = new File(request.getEngineInstallDir());
		if (!engineLogDirectory.isDirectory()) {
			throw new CollectLogfileException("The dir to collect the engines "
					+ "logs from does not exist.");
		}

		int leftAttempts = 3;
		while (leftAttempts > 0) {
			leftAttempts--;
			LogfileCollection lfc = new LogfileCollection();
			try {
				lfc.addEngineLogfiles(collectEngineLogfiles(engineLogDirectory));
			} catch (IOException exception) {
				log.error("Engine logfile could not be read:", exception);
			}

			try {
				lfc.addBetsyLogfiles(collectBetsyServerLogfiles(betsyServerInstallDir));
			} catch (IOException exception) {
				log.error("Engine logfile could not be read:", exception);
			}

			return lfc;
		}

		log.error("Logfiles could not be collected because the file's data "
				+ "could not be read.");
		throw new CollectLogfileException("Logfiles could not be collected "
				+ "because the file's data could not be read.");
	}

	private List<FileMessage> collectBetsyServerLogfiles(
			final File betsyServerInstallDir) throws IOException {
		List<FileMessage> list = new LinkedList<>();

		Collection<?> logfiles = FileUtils.listFiles(betsyServerInstallDir,
				TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		for (Object o : logfiles) {
			File file = (File) o;
			byte[] data = FileUtils.readFileToByteArray(file);
			FileMessage lf = new FileMessage(file.getName(), data,
					Checksum.createChecksum(data));
			list.add(lf);
		}

		if (list.isEmpty()) {
			log.warn("There were no betsy server logfiles to be collected.");
		}

		return list;
	}

	private List<FileMessage> collectEngineLogfiles(
			final File engineLogDirectory) throws IOException {
		List<FileMessage> list = new LinkedList<>();

		Collection<?> logfiles = FileUtils.listFiles(engineLogDirectory,
				TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		for (Object o : logfiles) {
			File file = (File) o;
			byte[] data = FileUtils.readFileToByteArray(file);
			FileMessage lf = new FileMessage(file.getName(), data,
					Checksum.createChecksum(data));
			list.add(lf);
		}

		if (list.isEmpty()) {
			log.warn("There were no engine logfiles to be collected.");
		}

		return list;
	}

}
