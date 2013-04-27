package betsy.virtual.host.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import betsy.Configuration;
import betsy.virtual.host.VBoxController;
import betsy.virtual.host.exceptions.DownloadException;
import betsy.virtual.host.exceptions.archive.ArchiveContentException;
import betsy.virtual.host.exceptions.archive.ArchiveException;
import betsy.virtual.host.exceptions.archive.ArchiveExtractionException;
import betsy.virtual.host.exceptions.archive.UnsupportedArchiveException;

/**
 * The {@link VirtualMachineImporter} offers several methods to prepare and
 * finally execute the import of a VirtualBox appliance.<br>
 * <br>
 * The default import procedure involves:
 * <ul>
 * <li>Download the VM</li>
 * <li>Extract the archive</li>
 * <li>Import the Appliance</li>
 * <li>Cleanup extraction path</li>
 * </ul>
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualMachineImporter {

	private static final Logger log = Logger.getLogger(VirtualMachineImporter.class);

	private final String archiveRequirements = "Please verify to meet the "
			+ "requirements of the archive structure. "
			+ "The archive must either contain"
			+ " the .ova file or the .ovf file. They can be either "
			+ "stored without or within a folder. If a folder is used it "
			+ "should be named just as the engine.";

	private final VBoxController vbc;
	private final String vmName;
	private final String engineName;
	private final File downloadPath;
	private final File extractPath;

	public VirtualMachineImporter(final String vmName, final String engineName,
			final File downloadPath, final File extractPath,
			final VBoxController vbc) {
		if (StringUtils.isBlank(vmName)) {
			throw new IllegalArgumentException("vmName must not be null or"
					+ " empty");
		}
		if (StringUtils.isBlank(engineName)) {
			throw new IllegalArgumentException("engineName must not be null "
					+ "or empty");
		}

		this.vbc = Objects.requireNonNull(vbc);
		this.extractPath = Objects.requireNonNull(extractPath);
		this.downloadPath = Objects.requireNonNull(downloadPath);

		this.vmName = vmName;
		this.engineName = engineName;

	}

	/**
	 * Default procedure to guide the import of a VirtualMachine.
	 * 
	 * @throws ArchiveException
	 *             thrown if the Archive is corrupted or did not contain the
	 *             required data
	 * @throws DownloadException
	 *             thrown if the download failed
	 */
	public void executeVirtualMachineImport() throws ArchiveException,
			DownloadException {
		log.debug("Importing " + vmName + "...");
		try {
			executeDownload();

			executeExtraction();

			vbc.importVirtualMachine(vmName, engineName, getVBoxImportFile());
			log.trace("...import finished!");
		} finally {
			// always clean the extraction directory
			try {
				cleanVMExtractPath();
			} catch (IOException e) {
				// ignore if can't clean up
			}
		}
	}

	private boolean isDownloaded() {
		File archive = getDownloadArchiveFile();
		return archive.isFile();
	}

	private boolean isExtractedAndHasImportableFiles()
			throws ArchiveContentException {
		File extractFolder = getEngineExtractPath();
		// VM's extraction folder must exist
		if (extractFolder.isDirectory()) {
			// and must have the required files in it
			// --> check for .ova OR .ovf
			return containsRequiredEngineFiles(extractFolder);
		}
		return false;
	}

	private boolean containsRequiredEngineFiles(File folder)
			throws ArchiveContentException {
		// ova and ovf must be unique, else we can't decide which one to take
		boolean ovf = false;
		boolean ova = false;
		for (Object o : FileUtils.listFiles(folder,
				new String[] { "ova", "ovf" }, true)) {
			File file = (File) o;
			if (file.getName().toLowerCase().endsWith(".ova")) {
				if (ova || ovf) {
					throw new ArchiveContentException("Could not determine "
							+ "which appliance should be imported. Please "
							+ "assure the archive contains only one appliance.");
				}
				ova = true;
				// return true;
			} else if (file.getName().toLowerCase().endsWith(".ovf")) {
				if (ovf || ova) {
					throw new ArchiveContentException("Could not determine "
							+ "which appliance should be imported. Please "
							+ "assure the archive contains only one appliance.");
				}
				ovf = true;
			}
		}
		if (ova || ovf) {
			return true;
		}

		return false;
	}

	public File getEngineExtractPath() {
		return new File(this.extractPath, vmName);
	}

	public URL getDownloadAddress() throws URIException, MalformedURLException {
		Configuration config = Configuration.getInstance();
		String address = config.getValueAsString("virtualisation.engines."
				+ engineName + ".download",
				"https://lspi.wiai.uni-bamberg.de/svn/betsy/vms/betsy-"
						+ engineName + ".tar.bz2");
		return new URL(URIUtil.encodeQuery(address));
	}

	public File getDownloadArchiveFile() {
		try {
			String url = getDownloadAddress().toString();
			// get only the filename + extension without the directory structure
			String fileName = url.substring(url.lastIndexOf('/') + 1,
					url.length());
			return new File(this.downloadPath, fileName);
		} catch (MalformedURLException | URIException exception) {
			log.warn("Error while resolving DownloadArchiveFile:", exception);
			// try default naming
			return new File(this.downloadPath, vmName + ".tar.bz2");
		}
	}

	/**
	 * Get the appliance file that should be used for the import. This is either
	 * an .ova or an .ovf file. Nevertheless there must be only one appliance
	 * file within the folder. If there is more than one file, we can't decide
	 * which one to take.
	 * 
	 * @return the appliance file to use
	 * @throws ArchiveContentException
	 *             thrown if there was either none or there were more than one
	 *             appliance files
	 */
	public File getVBoxImportFile() throws ArchiveContentException {
		// return either .ova OR .ovf file
		File vmDir = getEngineExtractPath();
		Collection<?> collection = FileUtils.listFiles(vmDir, new String[] {
				"ova", "ovf" }, true);

		// verify there is only one match
		if (collection.size() != 1) {
			if (collection.size() > 1) {
				throw new ArchiveContentException("Could not determine which "
						+ "appliance should be imported. Expected 1, got "
						+ collection.size() + ". Please "
						+ "assure the archive contains only one appliance.");
			} else {
				throw new ArchiveContentException("The extracted archive did "
						+ "not contain any importable appliance.");
			}
		}

		File importableFile = (File) collection.iterator().next();
		return importableFile;
	}

	private void cleanVMExtractPath() throws IOException {
		if (getEngineExtractPath().exists()) {
			FileUtils.forceDelete(getEngineExtractPath());
		}
	}

	private void executeExtraction() throws ArchiveException {
		boolean correctExtraction = false;
		try {
			correctExtraction = isExtractedAndHasImportableFiles();
		} catch (ArchiveContentException exception) {
			// ignore --> extract again...
		}

		if (!correctExtraction) {
			log.debug("Extracting " + vmName + "...");
			// extract
			List<File> extractedFiles = new LinkedList<>();
			try {
				// make sure extraction directory is empty --> clean
				cleanVMExtractPath();

				ArchiveExtractor ae = new ArchiveExtractor();
				extractedFiles = ae.ectractArchive(getDownloadArchiveFile(),
						this.extractPath);

				File[] pathFiles = this.extractPath.listFiles();
				List<File> pathFileList = Arrays.asList(pathFiles);

				/*
				 * Try to handle archives in which there is no single folder
				 * that wraps all the engine's files.
				 */
				if (isExtractedWithoutEngineDir(extractedFiles, pathFileList)) {
					moveFilesIntoVmDir(extractedFiles, pathFileList);
				}

			} catch (UnsupportedArchiveException | IOException exception) {
				try {
					cleanVMExtractPath();
				} catch (IOException e) {
					// can be ignored
				}
				// if files had been extracted to the top level, remove them too
				for (File delFile : extractedFiles) {
					try {
						FileUtils.forceDelete(delFile);
					} catch (IOException exception2) {
						// can be ignored
					}
				}
				throw new ArchiveExtractionException(
						"Could not extract the downloaded archive:", exception);
			}
			log.trace("...extraction finished!");

			// check if we got all relevant files
			if (!isExtractedAndHasImportableFiles()) {
				throw new ArchiveContentException("The extracted "
						+ "archive either did not contain the "
						+ "required files. " + archiveRequirements);
			}
		}
	}

	private boolean isExtractedWithoutEngineDir(List<File> extractedFiles,
			List<File> pathFileList) {
		List<File> matchedFiles = getExtractedFilesOnTopLevel(extractedFiles,
				pathFileList);

		if (matchedFiles.size() == 1 && matchedFiles.get(0).isDirectory()) {
			File eFile = matchedFiles.get(0);
			// is directory - with engine name ? --> wrapping right
			if (eFile.getName().equals(vmName)) {
				return false;
			} else {
				// false wrapping, folder has different naming
				return true;
			}
		} else {
			// if there is more than one file always wrap them into the engine
			// dir. Also if extracted single file is no directory
			return true;
		}
	}

	private List<File> getExtractedFilesOnTopLevel(List<File> extractedFiles,
			List<File> pathFileList) {
		List<File> matchedFiles = new LinkedList<>();
		for (File f : extractedFiles) {
			if (pathFileList.contains(f)) {
				matchedFiles.add(f);
			}
		}
		return matchedFiles;
	}

	private void moveFilesIntoVmDir(List<File> extractedFiles,
			List<File> pathFileList) throws IOException, ArchiveException {
		List<File> matchedFiles = getExtractedFilesOnTopLevel(extractedFiles,
				pathFileList);
		// only one element extracted that is a folder
		if (matchedFiles.size() == 1 && matchedFiles.get(0).isDirectory()) {
			File eFile = matchedFiles.get(0);
			// check if folder is named wrong --> renaming is enough
			if (containsRequiredEngineFiles(eFile)) {
				File newName = new File(eFile.getParentFile(), vmName);
				if (!eFile.renameTo(newName)) {
					// renaming failed
					throw new ArchiveExtractionException(
							"The archive can't be extracted correctly. "
									+ "The renaming of the extracted "
									+ "folder failed. " + archiveRequirements);
				}
			}
		} else {
			log.trace("Archive was not wrapped into folder, now move all "
					+ "files into VM's subdir");
			// no wrapping Folder --> must be moved to engine folder
			File eVmFolder = getEngineExtractPath();
			eVmFolder.mkdirs();

			for (File eFile : extractedFiles) {
				if (pathFileList.contains(eFile)) {
					if (eFile.isDirectory()) {
						// copy all directories recursively
						FileUtils.copyDirectory(eFile, new File(eVmFolder,
								eFile.getName()));
					} else {
						FileUtils.copyFileToDirectory(eFile, eVmFolder);
					}
					// file copied, then delete the original
					FileUtils.forceDelete(eFile);
				}
			}
		}
	}

	private void executeDownload() throws DownloadException {
		if (!isDownloaded()) {
			log.debug("Downloading " + vmName + "...");
			// download
			try {
				try {
					// creates relevant dirs and overwrites existing files
					FileUtils.copyURLToFile(getDownloadAddress(),
							getDownloadArchiveFile());
				} catch (IOException exception) {
					throw new DownloadException(String.format(
							"The download of '%s' failed:",
							getDownloadAddress().toExternalForm()), exception);
				}

			} catch (MalformedURLException | URIException exception) {
				throw new DownloadException("Could not download the virtual "
						+ engineName + " image:", exception);
			}
			log.trace("...download finished!");
		}
	}
}
