package messages;
import java.io.Serializable;


public class ClientHost implements Serializable {

	private static final long serialVersionUID = 1L;

	private String address;
	private int port;
	
	public ClientHost(String address, int port) {
		this.address = address;
		this.port    = port;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
}
