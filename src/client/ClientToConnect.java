package client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import server.Server;

import messages.ClientHost;
import messages.FileDescription;
import messages.Message;

/***
 * Class that will run in a different thread to connect another client in order to download a file fragment
 * @author razvan
 *
 */
public class ClientToConnect implements Runnable {

	static Logger log = Logger.getLogger(ClientToConnect.class.getName());
	
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	private String address;
	private int port;
	
	private int fragment;
	private FileDescription fileDescription;
	
	public ClientToConnect(String address, int port, int fragment, FileDescription fileDescription) {
		this.address  = address;
		this.port	  = port;
		
		this.fragment = fragment;
		this.fileDescription = fileDescription;
	}
	
	@Override
	public void run() {
		try {
			socket = new Socket(address, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
			/* send file fragment request message to another client */
			Message requestMsg = new Message();
			requestMsg.setFileDescription(fileDescription);
			requestMsg.setFragment(fragment);
			requestMsg.setClientHost(new ClientHost(address, port));
			oos.writeObject(requestMsg);
			
			try {
				Message fileMsg = (Message)ois.readObject();
				log.info("Received fragment: " + fragment + " from " + port);
				
				/* write received bytes to new local file */
				String pathToFile = "./../downloaded/" + fileDescription.getFileName();
				File file = new File(pathToFile);
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				raf.seek(fragment * fileDescription.getFragmentLength());
				raf.write(fileMsg.getFile());
				raf.close();
				socket.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
