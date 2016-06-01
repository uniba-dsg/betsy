package betsy.common.virtual.swarm.messages;

import java.io.Serializable;


/**
 * 
 * The {@link Message} represents a message sent or received.
 * 
 * @author Christoph Broeker
 * 
 * @version 1.0
 *
 */
public final class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private final String content;
	private final TcpMessageType messageType;

	/**
	 * 
	 * The {@link Message} represents a message sent or received.
	 * 
	 * @param content The content of the {@link Message}.
	 * @param messageType The {@link TcpMessageType} of the {@link Message}.
	 */
	public Message(String content, TcpMessageType messageType) {
		super();
		this.messageType = messageType; 
		this.content = content.trim();
	}
	
	/**
	 * 
	 * @return Returns the content of the {@link Message}.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 
	 * @return Returns the {@link TcpMessageType} of the {@link Message}.
	 */
	public TcpMessageType getMessageType() {
		return messageType;
	}

	/**
	 * @return Returns the content of the {@link Message}.
	 */
	@Override
	public String toString() {
		return content;
	}

	/**
     *
     * The enum for the messageTypes. These options are given: NAMEREQUEST,
     * NAMERESPONSE,  CONTENT.
     *
     * @author Christoph Broeker
     *
     * @version 1.0
     */

    public static enum TcpMessageType {
        NAMEREQUEST, NAMERESPONSE, CONTENT
    }
}
