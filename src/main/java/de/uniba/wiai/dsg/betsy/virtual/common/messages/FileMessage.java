package de.uniba.wiai.dsg.betsy.virtual.common.messages;

import java.io.Serializable;

import de.uniba.wiai.dsg.betsy.virtual.common.Checksum;

//TODO JAVADOC
public class FileMessage implements Serializable {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private final byte[] data;
	private final String filename;
	private final Checksum checksum;

	public FileMessage(final String filename, final byte[] data,
			final Checksum checksum) {
		if (filename == null || filename.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"filename must not be null or empty");
		}
		if (data == null) {
			throw new IllegalArgumentException("data must not be null");
		}
		if (checksum == null) {
			throw new IllegalArgumentException("checksum must not be null");
		}

		this.data = data;
		this.filename = filename;
		this.checksum = checksum;
	}

	public byte[] getData() {
		return data;
	}

	// +extension
	public String getFilename() {
		return filename;
	}

	public Checksum getChecksum() {
		return checksum;
	}

	public boolean isDataValid() {
		return Checksum.isValid(getData(), getChecksum());
	}
}
