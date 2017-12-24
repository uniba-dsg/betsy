package betsy.common.virtual.swarm.common;


import betsy.common.virtual.exceptions.NetworkException;
import betsy.common.virtual.exceptions.SystemException;
import betsy.common.virtual.swarm.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 
 * The {@link ConnectionService} handles the {@link ObjectInputStream} and the
 * {@link ObjectOutputStream} for a {@link Socket}.
 * 
 * @author Christoph Broeker
 * 
 * @version 1.0
 * 
 */
public class ConnectionService {

	private Socket socket;
	private ObjectOutputStream objectOutputStream = null;
	private ObjectInputStream objectInputStream = null;

	/**
	 * 
	 * The {@link ConnectionService} handles the {@link ObjectInputStream} and
	 * the {@link ObjectOutputStream} for one {@link Socket}.
	 * 
	 * 
	 * @param socket The {@link Socket} which the {@link ConnectionService} should handle.
	 * @throws NetworkException 
	 */
	public ConnectionService(Socket socket) throws NetworkException {
		this.socket = socket;
		try {
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			throw new NetworkException("Error by creating connection.");
		}
	}

	/**
	 * 
	 * Receives a {@link Object} from the {@link ObjectInputStream} and returns
	 * them.
	 * 
	 * @return Returns the received {@link Object}.
	 * @throws NetworkException In case of an error by receiving a {@link Message}.
	 * @throws SystemException In case of an error by cast the received {@link Object}.
	 */
	public Object receive() throws NetworkException, SystemException {
		Object object = null;
		try {
			object =  objectInputStream.readObject();
		} catch (IOException e) {
			throw new NetworkException("By receiving a message an error occurs.");
		} catch (ClassNotFoundException e) {
			throw new SystemException("A systemerror occurs by receiving a message.");
		}
		return object;
	}

	/**
	 * 
	 * Sends a {@link Object} via {@link ObjectOutputStream}.
	 * 
	 * @param object The {@link Object} which should be send.
	 * 
	 * @throws NetworkException In case of an error by sending a {@link Object}.
	 */
	public void send(Object object) throws NetworkException {
		try {
			objectOutputStream.writeObject(object);
			objectOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException("By sending a message an error occurs.");

		}

	}

	/**
	 * Closes the socket of the {@link ConnectionService}.
	 * 
	 * @throws NetworkException In case of an error closing the socket.
	 */
	public void close() throws NetworkException {
		try {
			socket.getInputStream().close();
			socket.getOutputStream().close();
			socket.close();
		} catch (IOException e) {
			throw new NetworkException("Can't close the socket.");
		}
	}
}
