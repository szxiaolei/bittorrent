package messages;

import java.io.Serializable;
import java.util.*;

/***
 * Class that contains the implementation of a message that will be sent thorugh the sockets.
 * @author razvan
 *
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String messageBody;
	private byte[] file;
	private FileDescription fileDescription;
	private ClientHost clientHost;
	private List<ClientHost> ownersOfFile;
	private int fragment;

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public FileDescription getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(FileDescription fileDescription) {
		this.fileDescription = fileDescription;
	}

	public ClientHost getClientHost() {
		return clientHost;
	}

	public void setClientHost(ClientHost clientHost) {
		this.clientHost = clientHost;
	}

	public List<ClientHost> getOwnersOfFile() {
		return ownersOfFile;
	}

	public void setOwnersOfFile(List<ClientHost> ownersOfFile) {
		this.ownersOfFile = ownersOfFile;
	}

	public int getFragment() {
		return fragment;
	}

	public void setFragment(int fragment) {
		this.fragment = fragment;
	}
}
