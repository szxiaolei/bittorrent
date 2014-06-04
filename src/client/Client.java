package client;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;

import server.Server;

import messages.*;

/**
 * Class which represents a client which can publish or retrieve a file.
 * @author razvan
 *
 */
public class Client extends AbstractClient {
	
	static Logger log = Logger.getLogger(Client.class.getName());
	
	private Socket socketToServer;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private String clientHost;
	private int clientPort;
	
	public static final int NR_FRAGMENTS = 3;
	
	public Client(String clientHost, int clientPort, String serverHost, int serverPort) throws UnknownHostException, IOException {
		this.clientHost = clientHost;
		this.clientPort = clientPort;
		
		Thread waiter = new Thread(new Waiter(clientPort));
		waiter.start();
		
		socketToServer = new Socket(serverHost, serverPort);
		oos = new ObjectOutputStream(socketToServer.getOutputStream());
		ois = new ObjectInputStream(socketToServer.getInputStream());
	}

	@Override
	public void publishFile(File file) throws IOException {
		String fileName = file.getName();
		long totalLength = file.length();
		long fragmentLength = (int)(totalLength / NR_FRAGMENTS);
		
		FileDescription fileDescription = new FileDescription(fileName, totalLength, fragmentLength);
		
		sendPublishMessageToServerForThisFile(fileDescription);
	}

	@Override
	public void retrieveFile(String fileName) throws IOException {
		Message retrieveMessage = new Message();
		retrieveMessage.setMessageBody("retrieve");
		retrieveMessage.setFileDescription(new FileDescription(fileName));
		oos.writeObject(retrieveMessage);
		
		try {
			Message retrieveResponse = (Message)ois.readObject();
			contactClientsAndDownloadFile(retrieveResponse.getFileDescription(), retrieveResponse.getOwnersOfFile());			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Method that for each fragment of the file choose a different client to download it
	 * @param fileDescription Description of the file
	 * @param owners List of clients that own the file
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void contactClientsAndDownloadFile(FileDescription fileDescription, 
					List<ClientHost> owners) throws UnknownHostException, IOException {
		
		List<Thread> threadList = new ArrayList<Thread>();
		ClientHost ownerClient = null;
		int ownersListIndex = 0;
		for(int i = 0; i < NR_FRAGMENTS; i++) {
			ownerClient = owners.get(ownersListIndex);
			
			Thread thread = new Thread(new ClientToConnect(ownerClient.getAddress(), 
															ownerClient.getPort(), 
															i, 
															fileDescription));
			threadList.add(thread);
			thread.start();
			
			ownersListIndex ++;
			if(ownersListIndex == owners.size()) {
				ownersListIndex = 0;
			}
		}
		
		/* wait for threads to finish downloading */
		for(Thread thread : threadList) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/* inform the server that you have a new file to share */
		sendPublishMessageToServerForThisFile(fileDescription);
	}
	
	/**
	 * Method that sends a message to the server to publish a file just downloaded
	 * @param fileDescription
	 */
	private void sendPublishMessageToServerForThisFile(FileDescription fileDescription) {
		
		Message publishMessage = new Message();
		publishMessage.setMessageBody("publish");
		publishMessage.setFileDescription(fileDescription);
		publishMessage.setClientHost(new ClientHost(clientHost, clientPort));
		try {
			oos.writeObject(publishMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * This class is a thread that will run for every client registered to system in order to wait for other
	 * clients to connect and download files fragments.
	 * @author razvan
	 *
	 */
	private final class Waiter implements Runnable {

		ServerSocket socket;
		
		public Waiter(int port) throws IOException {
			socket = new ServerSocket(port);
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					log.info("Waiting...");
					Socket socketToWait = socket.accept();
					log.info("Client connected!");
					ObjectOutputStream oosToClient = new ObjectOutputStream(socketToWait.getOutputStream());
					ObjectInputStream oisToClient = new ObjectInputStream(socketToWait.getInputStream());
					
					Message requestMsg = (Message)oisToClient.readObject();
					log.info("Request received!");
					
					/* read file to send */
					String pathToFile = "./../files/" + requestMsg.getFileDescription().getFileName();
					File file = new File(pathToFile);
					
					int fragment = requestMsg.getFragment();
					long fragmentLength = requestMsg.getFileDescription().getFragmentLength();
					
					int toSend = (int)fragmentLength;
					if((fragment + 1) * fragmentLength > (int) file.length()) {
						toSend = (int) file.length() - fragment * (int)fragmentLength;
					}
					
				    byte[] fileData = new byte[toSend];
				    RandomAccessFile raf = new RandomAccessFile(file, "r");
				    raf.seek(fragment * (int)fragmentLength);
				    raf.read(fileData);
				    raf.close();
					
				    /* send file to client */
				    Message fileResponse = new Message();
				    fileResponse.setFile(fileData);
				    fileResponse.setClientHost(requestMsg.getClientHost());
					oosToClient.writeObject(fileResponse);
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
