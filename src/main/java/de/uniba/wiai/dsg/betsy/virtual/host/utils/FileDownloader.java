package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.DownloadException;

public class FileDownloader {

	private final Logger log = Logger.getLogger(getClass());
	
	public void downloadFile(final URL url, final File destination) throws DownloadException {
		try {
			//creates relevant dirs and overwrites existing files
			FileUtils.copyURLToFile(url, destination);
		} catch (IOException exception) {
			log.error(String.format("The download of '%s' failed:", url.toExternalForm()), exception);
			throw new DownloadException(String.format("The download of '%s' failed:", url.toExternalForm()), exception);
		}
	}
	
}
