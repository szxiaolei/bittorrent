package test;

import java.net.InetAddress;

import client.Client;

public class RetrieveClient1 {

	public static String fileName1 = "kids.jpg";
	public static String fileName2 = "bestalgs.pdf";
	
	public static void main(String argv[]) {
		
		System.out.println("[TestClient2]: Starting testing client 2 (this one retreive a file).");
		if (argv.length < 2) {	
			System.out.println("[TestClient2]: The client starts with command: java Client ServerHost ServerPort ClientPort");
			System.exit(-1);
		} 		
		try {
			
			/* adresarea serverului (ip, port) */
			String serverHost = argv[0];
			int serverPort = Integer.parseInt(argv[1]);
			
			/* adresarea clientului (ip, port) */
			String clientHost = InetAddress.getLocalHost().getHostName();
			int clientPort = Integer.parseInt(argv[2]);

			Client cli = new Client(clientHost,clientPort,serverHost,serverPort);
			cli.retrieveFile(fileName1);
			System.out.println("[TestClient2]: File " + fileName1 + " was retrieved... DONE!");
			cli.retrieveFile(fileName2);
			System.out.println("[TestClient2]: File " + fileName2 + " was retrieved... DONE!");

		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}