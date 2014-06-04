package server;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.Logger;

import messages.*;


public class Server implements Runnable {
	
	static Logger log = Logger.getLogger(Server.class.getName());
	
	Map<FileDescription, List<ClientHost>> files;
	
	private ServerSocket socket;
	
	public Server(int portForClients) throws IOException {
		socket = new ServerSocket(portForClients);
		files = new HashMap<FileDescription, List<ClientHost>>();
	}
	
	public static void main(String[] args) throws IOException {
		
		if(args.length < 1) { 
			log.info("Usage: run-server <PortForClients>");
			return;
		}
		
		int portForClients = Integer.parseInt(args[0]);
		Server server = new Server(portForClients);
		server.run();

	}

	@Override
	public void run() {
		while(true) {
			try {
				Socket clientSocket = socket.accept();
				log.info("Client connected!");
				Thread clientThread = new Thread(new ClientThread(clientSocket));
				clientThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private final class ClientThread implements Runnable {

		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		
		public ClientThread(Socket clientSocket) throws IOException {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					Message message = (Message)ois.readObject();					
					/* receiving publish message */
					if(message.getMessageBody().equals("publish")) {
						log.info("publish " + message.getFileDescription().getFileName());
						addFileAndClient(message.getFileDescription(), message.getClientHost());
					}
					/* receiving retrieve message */
					else if(message.getMessageBody().equals("retrieve")) {
						log.info("retrieve " + message.getFileDescription().getFileName());
						Message retrieveResponse = new Message();
						
						/* search for file in the files map */
						for(Map.Entry<FileDescription, List<ClientHost>> entry : files.entrySet()) {
							if(entry.getKey().getFileName().equals(message.getFileDescription().getFileName())) {
								retrieveResponse.setFileDescription(entry.getKey());
								retrieveResponse.setOwnersOfFile(entry.getValue());
							}
						}

						oos.writeObject(retrieveResponse);
					}
					
				} catch (IOException e) {
					log.info("Connection lost!");
					return;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Method that adds a file in the published files map, or if already exists just adds the new client
		 * to the list of clients of this file
		 * @param fileDescription
		 * @param clientHost
		 */
		private void addFileAndClient(FileDescription fileDescription, ClientHost clientHost) {
			
			for(Map.Entry<FileDescription, List<ClientHost>> entry : files.entrySet()) {
				if(entry.getKey().getFileName().equals(fileDescription.getFileName())) {
					entry.getValue().add(clientHost);
					return;
				}
			}
			
			List<ClientHost> clients = new ArrayList<ClientHost>();
			clients.add(clientHost);
			files.put(fileDescription, clients);
		}
	}
}
