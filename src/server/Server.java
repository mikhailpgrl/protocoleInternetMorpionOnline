package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import client.ClientModel;


public class Server {

	public static int id = 0;

	// Liste de clients connectes au serveur
	public static List<ClientModel> clients;

	// Les joueurs en attente de reponse des autres clients
	public static Map<String, String> queue;

	
	
	
	
	private static ExecutorService executor = Executors.newFixedThreadPool(5);


	/**
	 * On utilise le threadPool afin de pouvoir gerer les clients avec 5 threads seulement
	 * @param argv
	 */
	@SuppressWarnings("resource")
	public static void main(String argv[]) {
		try{
			clients = new ArrayList<>();
			queue = new HashMap<>();
			ServerSocket serverSocket = new ServerSocket(1027);
			System.out.println("Server started: @"+ serverSocket.getInetAddress().getHostAddress().toString() +" port:" + serverSocket.getLocalPort());
			while(true){        	
				Socket s = serverSocket.accept();
				id++;
				ClientModel cm = new ClientModel(String.valueOf(id), s);
				System.out.println("Le client :" + s.getInetAddress() + " vient de se connecter, son id : " + cm.getId());
				//ClientModel cm2 = new ClientModel(String.valueOf(2), s);
				//queue.put("1", "2");
				Runnable clientRun = new ServerHandlerThread(cm);
				clients.add(cm);
				executor.execute(clientRun);
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage().toString());
		}
	}



}
