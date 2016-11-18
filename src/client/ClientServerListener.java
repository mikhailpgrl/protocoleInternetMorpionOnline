package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import client.Client.current_role;
import client.Client.Current_state;
import game.Platforme;
import server.ServerHandlerThread;

/**
 * 
 * @author POGORELOV Mikhail et CHIEV Alexandre
 * 
 * Runnable qui gere la connexion entre le Client et le Server
 *
 */


public class ClientServerListener implements Runnable{
	
	
	public static enum ClientSL_current_state {
		nothing,
		//waiting_error_create_addr,
		waiting_ok,
		waiting_yourid,
		waiting_ok_exit
	}
	
	public static ClientSL_current_state myState;
	
	
	private OutputStream osServ;
	private InputStream is;
	// Socket de connexion au server de jeu (autre client)
	private Socket gameServerSocket;

	public ClientServerListener(OutputStream os, InputStream is) {
		super();
		this.myState = ClientSL_current_state.nothing;
		this.osServ = os;
		this.is = is;
	}

	@Override
	public void run() {
		BufferedReader myIsr;
		try {
			myIsr = new BufferedReader(new InputStreamReader(is));
			char[] buf = new char[1024];;
			while (true){
				String line;
				while((line = myIsr.readLine()) != null) {
					receiveMessage(line,osServ);
				}
			}
		}
		catch (IOException e1) {
			if("Socket closed".compareTo(e1.getMessage().toString()) == 0){
				System.out.println("Le client s'est d�connect�");
			}else{
				System.out.println(e1.getMessage().toString());
				e1.printStackTrace();	
			}
		}
	}

	/**
	 * Traite les messages recu par le serveur
	 * @param message
	 * @param os
	 */
	public void receiveMessage(String message, OutputStream os){
		String[] parts = message.split(" ");
		String msgPart2 = "";
		if(parts.length > 1){
			msgPart2 = parts[1]; 
		}
		String msg = parts[0];
		System.out.println("Message recu " + message);
		// ATTENTE D'UN ETAT PARTICULIER
		if(myState == ClientSL_current_state.waiting_ok){
			if(msg.compareTo(Client.ok) == 0){
				System.out.println("in state waiting_ok completed");
				myState = ClientSL_current_state.nothing;
			}else{
				System.out.println("J'attend la reponse ok du server!");
			}
		}
		if(myState == ClientSL_current_state.waiting_yourid){
			if(msg.compareTo(Client.yourId) == 0){
				System.out.println("in state waitin your id  completed");
				System.out.println("Votre ID " + msgPart2);
				myState = ClientSL_current_state.nothing;
			}else{
				System.out.println("J'attend la reponse ok du server!");
			}
		}
		if(myState == ClientSL_current_state.waiting_ok_exit){
			if(msg.compareTo(Client.ok) == 0){
				System.out.println("in state waiting_ok_exit completed");
				myState = ClientSL_current_state.nothing;
				System.out.println("EXIT");
			}else{
				System.out.println("J'attend la reponse ok du server!");
			}
		}
		// FIN D'ATTENTE D'UN ETAT PARTICULIER
		if(myState == ClientSL_current_state.nothing){
			switch (msg) {
			// Affiche les ids des joueurs
			case ServerHandlerThread.list_available:
				Client.sendMsgToServer(Client.ok, os);
				showMessage(message);
				break;
				// Affiche le message en entier	
			case ServerHandlerThread.error:
				showMessage(message);
				break;
			case ServerHandlerThread.ask_game:
				System.out.println("Voulez vous jouer avec le jouer : "  + message);
				break;
			case ServerHandlerThread.refuse_game:
				System.out.println("Votre demande a ete refusee");
				break;
			case ServerHandlerThread.adress_game:
				System.out.println("Vous avez recu l'adresse pour vous connecter: " + msgPart2);
				connectToTheGameServer(msgPart2);
				break;
//			case ServerHandlerThread.your_id:
//				System.out.println("Votre ID " + msgPart2);
//				break;	
			case ServerHandlerThread.create_server:
				/**
				 * Reponse temporaire pour cette version de client
				 */
				System.out.println("Je cree le serveur" + msgPart2);
				System.out.println("Envoyez le numero du port!");
				
				createServer(os);
//				String tmpRep = Client.server_adress + " " + "@add" + ":port";
//				try {
//					os.write(tmpRep.getBytes());
//					os.write("\n".getBytes());
//					os.flush();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				break;
			default:
				break;
			}
		}		
	}


	/**
	 * Port : 1028 // A modifier si besoin (ou à passer en argument au lancement du client)
	 */
	private void createServer(OutputStream os){
		System.out.println("createServer: starting");
		try {
			ServerSocket serverSocket = new ServerSocket(1028);
//			String reponse = Client.adressGame;
//			reponse+= " " + serverSocket.getInetAddress().getHostAddress().toString()+":"+"1028";
//			System.out.println("Le message du client serveur: " + reponse);
//			os.write(reponse.getBytes());
//			os.write("\n".getBytes());
//			os.flush();
//			System.out.println("Message envoy� = " + reponse);
			// Attente de connexion
			gameServerSocket = serverSocket.accept();
			System.out.println("Client connected");
			Client.myState = Client.Current_state.client_client;
			Client.myRole = current_role.server;
			Client.myPlatforme = new Platforme();
			Client.myPlatforme.show();
			if(Client.myPlatforme == null){
				System.out.println("myPlatforme is null");
			}
			startClientClientListener(gameServerSocket.getOutputStream(), gameServerSocket.getInputStream(), true);
			// Debut de la partie
		} catch (IOException e) {
			System.out.println(e.getMessage().toString());
			e.printStackTrace();
		}
	}

	/**
	 * Con
	 * @param addPort
	 */
	private void connectToTheGameServer(String addPort){		
		String[] add = addPort.split(":");
		String address = add[0];
		String port = add[1];
		System.out.println("connectToTheGameServer: @:" + address + ", port:" + port);
		try{
			this.gameServerSocket = new Socket(address,Integer.valueOf(port));
			System.out.println("connectToTheGameServer: connected");
			Client.myState = Client.Current_state.client_client;
			Client.myRole = current_role.client;
			Client.myPlatforme = new Platforme();
			//Client.myPlatforme.show();
			if(Client.myPlatforme == null){
				System.out.println("ici null");
			}
			startClientClientListener(this.getGameServerSocket().getOutputStream(), this.getGameServerSocket().getInputStream(), false);
		}catch (IOException e) {
			System.out.println("client message: " + e.getMessage().toString());
		}
	}

	
	private static void startClientClientListener(OutputStream os, InputStream is, boolean isServer){
		Client.myClientClientListener = new ClientClientListener(os, is,isServer);
		new Thread(Client.myClientClientListener).start();
	}
	
	
	/**
	 * Affiche les message
	 * @param message : message a afficher
	 */
	private void showMessage(String message){
		System.out.println(message);
	}

	public Socket getGameServerSocket() {
		return gameServerSocket;
	}

	public void setGameServerSocket(Socket gameServerSocket) {
		this.gameServerSocket = gameServerSocket;
	}
	
	
	



}
