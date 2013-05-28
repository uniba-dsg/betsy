package betsy.virtual.host.virtualbox.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import betsy.virtual.host.exceptions.archive.ArchiveExtractionException;
import betsy.virtual.host.exceptions.archive.UnsupportedArchiveException;

/**
 * The {@link ArchiveExtractor} offers methods to extract a File that represents
 * an archive. Currently .zip and .ova archives are supported.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class ArchiveExtractor {

	private static final Logger log = Logger.getLogger(ArchiveExtractor.class);

	/**
	 * Extract the archive into the outputDir. Determine automatically which
	 * algorithm should be used.
	 * 
	 * @param inputFile
	 *            file to extract
	 * @param outputDir
	 *            where to extract the archive to
	 * @return a {@link List} of all extracted files.
	 * 
	 * @throws UnsupportedArchiveException
	 *             thrown if the file's extension indicated the archive is not
	 *             supported
	 * @throws ArchiveExtractionException
	 *             thrown if the extraction failed
	 */
	public List<File> extractArchive(final File inputFile, final File outputDir)
			throws UnsupportedArchiveException, ArchiveExtractionException {
		if (inputFile.getAbsolutePath().toLowerCase().endsWith(".zip")) {
			return extractZip(inputFile, outputDir);
		} else if (inputFile.getAbsolutePath().toLowerCase().endsWith(".ova")) {
			return extractOva(inputFile, outputDir);
		} else {
			throw new UnsupportedArchiveException("Archive type is not "
					+ "supported and can't be extracted! Either use another "
					+ "type or extract the archive manually. Supported types "
					+ "are: '.ova and .zip'");
		}
	}

	/**
	 * Extract the archive into the outputDir.
	 * 
	 * @param inputFile
	 *            file to extract
	 * @param outputDir
	 *            where to extract the archive to
	 * @return a {@link List} of all extracted files.
	 * 
	 * @throws ArchiveExtractionException
	 *             thrown if the extraction failed
	 */
	public List<File> extractOva(final File inputFile, final File outputDir)
			throws ArchiveExtractionException {
		// validate file ends with .ova
		if (!inputFile.getAbsolutePath().toLowerCase().endsWith(".ova")) {
			throw new IllegalArgumentException("invalid archive: "
					+ "inputFile must end with file extension of '.ova'");
		}
		// assure outputDir exists
		outputDir.mkdirs();

		// .ova mustn't be extracted, just copy the file into the outputDir
		try {
			FileUtils.copyFileToDirectory(inputFile, outputDir);
			File destination = new File(outputDir, inputFile.getName());
			List<File> list = new LinkedList<>();
			list.add(destination);
			return list;
		} catch (IOException exception) {
			throw new ArchiveExtractionException(
					"Exception while extracting .ova archive", exception);
		}
	}

	/**
	 * Extract the archive into the outputDir.
	 * 
	 * @param inputFile
	 *            file to extract
	 * @param outputDir
	 *            where to extract the archive to
	 * @return a {@link List} of all extracted files.
	 * 
	 * @throws ArchiveExtractionException
	 *             thrown if the extraction failed
	 */
	public List<File> extractZip(final File inputFile, final File outputDir)
			throws ArchiveExtractionException {
		// validate file ends with .zip
		if (!inputFile.getAbsolutePath().toLowerCase().endsWith(".zip")) {
			throw new IllegalArgumentException("invalid archive: "
					+ "inputFile must end with file extension of '.zip'");
		}
		// assure outputDir exists
		outputDir.mkdirs();

		try {
			List<File> extractedFiles = new LinkedList<>();
			ZipFile zf = new ZipFile(inputFile);
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();
				log.trace("--" + ze.getName());
				extractedFiles.add(extractZipEntry(outputDir, zf, ze));
			}

			return extractedFiles;
		} catch (IOException exception) {
			throw new ArchiveExtractionException(
					"Exception while extracting .zip archive", exception);
		}
	}

	private File extractZipEntry(final File target, final ZipFile zf,
			final ZipEntry ze) throws ArchiveExtractionException {
		File file = new File(target, ze.getName());
		log.trace("++" + file.getAbsolutePath());
		if (ze.isDirectory()) {
			file.mkdirs();
		} else {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				bis = new BufferedInputStream(zf.getInputStream(ze));
				bos = new BufferedOutputStream(new FileOutputStream(file));
				IOUtils.copy(bis, bos);
			} catch (IOException exception) {
				throw new ArchiveExtractionException(
						"Exception while saving ZipEntry:", exception);
			} finally {
				if (bos != null) {
					try {
						bos.close();
					} catch (IOException e) {
						// ignore, is being closed anyway
					}
				}
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
						// ignore, is being closed anyway
					}
				}
			}
		}
		return file;
	}
}
