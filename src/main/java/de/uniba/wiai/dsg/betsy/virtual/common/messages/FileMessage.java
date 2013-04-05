package de.uniba.wiai.dsg.betsy.virtual.common.messages;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

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
		if (StringUtils.isBlank(filename)) {
			throw new IllegalArgumentException(
					"filename must not be null or empty");
		}

		this.filename = filename;
		this.data = Objects.requireNonNull(data);
		this.checksum = Objects.requireNonNull(checksum);
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
