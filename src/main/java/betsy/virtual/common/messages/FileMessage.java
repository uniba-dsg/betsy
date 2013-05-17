package betsy.virtual.common.messages;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import betsy.virtual.common.Checksum;


/**
 * A {@link FileMessage} contains the binary data and the name (with extension)
 * of a file. Additionally a Checksum is created to verify the integrity of the
 * data after sending this {@link FileMessage} over a network.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class FileMessage implements Serializable {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private final byte[] data;
	private final String filename;
	private final Checksum checksum;

	public FileMessage(final String filename, final byte[] data) {
		if (StringUtils.isBlank(filename)) {
			throw new IllegalArgumentException(
					"filename must not be null or empty");
		}

		this.filename = filename;
		this.data = Objects.requireNonNull(data);
		this.checksum = Checksum.createChecksum(data);
	}

	public byte[] getData() {
		return data;
	}

	public String getFilename() {
		return filename;
	}

	public Checksum getChecksum() {
		return checksum;
	}

	/**
	 * Check whether the data is still equal to the time it was read.
	 * 
	 * @return true if data is valid, false if parts have been lost/changed
	 */
	public boolean isDataValid() {
		return Checksum.isValid(getData(), getChecksum());
	}
	
	@Override
	public String toString() {
		return "FileMessage for '"+filename+"'";
	}
}
