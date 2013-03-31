package de.uniba.wiai.dsg.betsy.virtual.common;

import java.io.Serializable;
import java.util.zip.CRC32;

/**
 * A checksum representation of the given data.<br>
 * Can be used to compare if sent and received data are equal or if information has been lost or corrupted.
 * <br>
 * @author Cedric Roeck
 * @version 1.0
 * 
 */
public class Checksum implements Serializable {

	/**
	 * SerialVersioUID.
	 */
	private static final long serialVersionUID = 1L;
	
	private long data;
	
	/**
	 * Create a new instance of the object.
	 * <br>
	 * @param data {@link Checksum} is created based on the given data
	 */
	public Checksum(final byte[] data) {
		if(data == null) {
			throw new IllegalArgumentException("data must not be null");
		}
		
		CRC32 crc = new CRC32();
		crc.update(data);
		this.data = crc.getValue();
	}
	
	@Override
	public boolean equals(Object otherO) {
		if(this == otherO) {
			return true;
		}
		if(otherO == null) {
			return false;
		}
		if(!(otherO instanceof Checksum)) {
			return false;
		}
		
		Checksum other = (Checksum) otherO;
		
		return this.data == other.getData();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(data);
		return result;
	}
	
	/**
	 * Get the checksum value as primitive long representation.
	 * 
	 * @return raw checksum value
	 */
	public long getData() {
		return this.data;
	}
}
