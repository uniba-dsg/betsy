package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.archive.ArchiveExtractionException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.archive.UnsupportedArchiveException;

/**
 * The {@link ArchiveExtractor} offers methods to extract a File that represents
 * an archive. Currently .tar.bz2, .zip and .ova archives are supported.
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
	public List<File> ectractArchive(final File inputFile, final File outputDir)
			throws UnsupportedArchiveException, ArchiveExtractionException {
		if (inputFile.getAbsolutePath().toLowerCase().endsWith(".tar.bz2")) {
			return extractTarBz2(inputFile, outputDir);
		} else if (inputFile.getAbsolutePath().toLowerCase().endsWith(".zip")) {
			return extractZip(inputFile, outputDir);
		} else if (inputFile.getAbsolutePath().toLowerCase().endsWith(".ova")) {
			return extractOva(inputFile, outputDir);
		} else {
			throw new UnsupportedArchiveException(
					"Archive type is not supported and can't be extracted! "
							+ "Either use another type or extract the archive manually. "
							+ "Supported types are: '.tar.bz2 and .zip'");
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
	public List<File> extractTarBz2(final File inputFile, final File outputDir)
			throws ArchiveExtractionException {
		// validate file ends with .tar.bz2
		if (!inputFile.getAbsolutePath().toLowerCase().endsWith(".tar.bz2")) {
			throw new IllegalArgumentException("invalid archive: "
					+ "inputFile must end with file extension of '.tar.bz2'");
		}
		// assure outputDir exists
		outputDir.mkdirs();

		FileOutputStream out = null;
		BZip2CompressorInputStream bzIn = null;
		try {
			FileInputStream fin = new FileInputStream(inputFile);
			BufferedInputStream in = new BufferedInputStream(fin);
			String fileName = inputFile.getName();
			String tarFilePath = fileName.substring(0,
					fileName.lastIndexOf(".tar.bz2"))
					+ ".tar";
			out = new FileOutputStream(tarFilePath);
			bzIn = new BZip2CompressorInputStream(in);
			final byte[] buffer = new byte[8192];
			int n = 0;
			while (-1 != (n = bzIn.read(buffer))) {
				out.write(buffer, 0, n);
			}

			return this.extractTar(new File(tarFilePath), outputDir);
		} catch (ArchiveExtractionException | IOException exception) {
			throw new ArchiveExtractionException(
					"Exception while extracting .tar.bz2 archive", exception);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// ignore, is being closed anyway
				}
			}
			if (bzIn != null) {
				try {
					bzIn.close();
				} catch (IOException e) {
					// ignore, is being closed anyway
				}
			}
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

	/**
	 * Untar the content of the input tar-file into an output folder.
	 * 
	 * @param inputFile
	 *            the input tar-file.
	 * @param outputDir
	 *            the output directory.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 *             thrown if inputFile could not be found.
	 * @throws ArchiveException
	 * 
	 * @return The {@link List} of {@link File}s with the extracted content.
	 */
	public List<File> extractTar(final File inputFile, final File outputDir)
			throws ArchiveExtractionException {
		// validate file ends with .tar
		if (!inputFile.getAbsolutePath().toLowerCase().endsWith(".tar")) {
			throw new IllegalArgumentException("invalid archive: "
					+ "inputFile must end with file extension of '.tar'");
		}
		// assure outputDir exists
		outputDir.mkdirs();

		InputStream inputStream = null;
		TarArchiveInputStream tarInputStream = null;

		try {
			log.debug(String.format("Untaring %s into %s.",
					inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

			final List<File> extractedFiles = new LinkedList<File>();
			inputStream = new FileInputStream(inputFile);
			tarInputStream = (TarArchiveInputStream) new ArchiveStreamFactory()
					.createArchiveInputStream("tar", inputStream);

			TarArchiveEntry entry = null;
			while ((entry = (TarArchiveEntry) tarInputStream.getNextEntry()) != null) {
				// create a new file below the given directory
				final File outFile = new File(outputDir, entry.getName());
				if (entry.isDirectory()) {
					if (!outFile.exists()) {
						log.debug(String.format("Creating new directory %s.",
								outFile.getAbsolutePath()));
						if (!outFile.mkdirs()) {
							throw new ArchiveExtractionException(String.format(
									"Couldn't create directory %s.",
									outFile.getAbsolutePath()));
						}
					}
				} else {
					log.debug(String.format("Creating new file %s.",
							outFile.getAbsolutePath()));
					OutputStream outputFileStream = null;
					try {
						outputFileStream = new FileOutputStream(outFile);
						IOUtils.copy(tarInputStream, outputFileStream);
					} finally {
						if (outputFileStream != null) {
							try {
								outputFileStream.close();
							} catch (IOException e) {
								// ignore, is being closed anyway
							}
						}
					}
				}
				extractedFiles.add(outFile);
			}
			// ... and done!
			return extractedFiles;
		} catch (ArchiveException | IOException exception) {
			throw new ArchiveExtractionException(
					"Exception while extracting .tar archive", exception);
		} finally {
			// close all resources if an exception is raised
			if (tarInputStream != null) {
				try {
					tarInputStream.close();
				} catch (IOException e) {
					// ignore, is being closed anyway
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// ignore, is being closed anyway
				}
			}
		}
	}

}
