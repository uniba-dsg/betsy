package betsy.virtual.common;

import java.io.Serializable;
import java.util.zip.CRC32;

/**
 * A checksum representation of the given data.<br>
 * Can be used to compare if sent and received data are equal or if information
 * has been lost or corrupted. <br>
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class Checksum implements Serializable {

	/**
	 * Create a {@link Checksum} based on the given data.
	 * 
	 * @param data
	 *            used to calculate the checksum
	 * @return the calculated checksum
	 */
	public static Checksum createChecksum(final byte[] data) {
		return new Checksum(data);
	}

	/**
	 * Check whether the given data does match the given checksum.
	 * 
	 * @param data
	 *            will be used to calculate a new checksum
	 * @param checksum
	 *            comparison reference, data must match this checksum
	 * @return true if data matches checksum, false if checksum belongs to
	 *         different data
	 */
	public static boolean isValid(final byte[] data, Checksum checksum) {
		return checksum.equals(new Checksum(data));
	}

	/**
	 * SerialVersioUID.
	 */
	private static final long serialVersionUID = 1L;

	private long data;

	/**
	 * Create a new instance of the object. <br>
	 * 
	 * @param data
	 *            {@link Checksum} is created based on the given data
	 */
	private Checksum(final byte[] data) {
		if (data == null) {
			throw new IllegalArgumentException("data must not be null");
		}

		CRC32 crc = new CRC32();
		crc.update(data);
		this.data = crc.getValue();
	}

	@Override
	public boolean equals(Object otherO) {
		if (this == otherO) {
			return true;
		}
		if (otherO == null) {
			return false;
		}
		if (!(otherO instanceof Checksum)) {
			return false;
		}

		Checksum other = (Checksum) otherO;

		return this.data == other.getValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(data);
		return result;
	}
	
	@Override
	public String toString() {
		return Long.toString(getValue());
	}

	/**
	 * Get the checksum value as primitive long representation.
	 * 
	 * @return raw checksum value
	 */
	private long getValue() {
		return this.data;
	}
}
