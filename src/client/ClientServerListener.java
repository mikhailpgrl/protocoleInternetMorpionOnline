package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import client.Client.current_role;
import game.Platforme;
import server.ServerHandlerThread;
import utils.CSLState;
import utils.UtilsClient;

/**
 * 
 * @author POGORELOV Mikhail et CHIEV Alexandre
 * 
 *         Runnable qui gere la connexion entre le Client et le Server
 *
 */

public class ClientServerListener implements Runnable {

	// TODO: Mettre la variable en private
	public CSLState myState;

	// Socket de communication avec le serveur
	private Socket socket;
	private static OutputStream osServ;
	private InputStream is;

	// Socket de connexion au server de jeu (autre client)
	private Socket gameServerSocket;

	public ClientServerListener(Socket socket) {
		super();
		this.myState = CSLState.nothing;
		this.socket = socket;
		try {
			ClientServerListener.osServ = socket.getOutputStream();
			this.is = socket.getInputStream();
		} catch (IOException e) {
			System.out.println(e.getMessage().toString());
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		BufferedReader myIsr;
		try {
			myIsr = new BufferedReader(new InputStreamReader(is));
			char[] buf = new char[1024];
			;
			while (true) {
				String line;
				while ((line = myIsr.readLine()) != null) {
					receiveMessage(line, osServ);
				}
			}
		} catch (IOException e1) {
			if ("Socket closed".compareTo(e1.getMessage().toString()) == 0) {
				//System.out.println("Socket client-serveur ferme...");
			} else {
				System.out.println(e1.getMessage().toString());
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Traite les messages recu par le serveur
	 * 
	 * @param message
	 * @param os
	 */
	public void receiveMessage(String message, OutputStream os) {
		String[] parts = message.split(" ");
		String msgPart2 = "";
		if (parts.length > 1) {
			msgPart2 = parts[1];
		}
		String msg = parts[0];
		System.out.println("Message recu " + message);
		/**
		 * On traite les reponses en fonction de l'état du client
		 */
		if (myState == CSLState.waiting_ok) {
			if (msg.compareTo(Client.ok) == 0) {
				System.out.println("in state waiting_ok completed");
				myState = CSLState.nothing;
			} else {
				System.out.println("J'attend la reponse ok du server!");
			}
		}
		if (myState == CSLState.waiting_yourid) {
			if (msg.compareTo(Client.yourId) == 0) {
				System.out.println("in state waiting your id  completed");
				System.out.println("Votre ID " + msgPart2);
				myState = CSLState.nothing;
				Client.sendMsgToServer(Client.ok, os);
			} else {
				System.out.println("J'attend la reponse ok du server!");
			}
		}
		if (myState == CSLState.waiting_ok_exit) {
			if (msg.compareTo(Client.ok) == 0) {
				System.out.println("in state waiting_ok_exit completed");
				myState = CSLState.nothing;
				try {
					/**
					 * Je met isRunning à false
					 * puis je demande de taper entrer pour quitter 
					 */
					Client.isRunning = false;
					socket.close();
					System.out.println("Tapez 'entrée' pour sortir...");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("J'attend la reponse ok du server!");
			}
		}
		if (myState == CSLState.waiting_refuse_error_create_addr || myState == CSLState.waiting_error_create_server) {
			switch (msg) {
			case ServerHandlerThread.refuse_game:
				if (myState == CSLState.waiting_refuse_error_create_addr) {
					System.out.println("Votre demande a ete refusee");
				} else {
					System.out.println("Vous avez envoyé Answer Y, donc vous ne pouvez " + "pas recevoir RefuseGame");
				}
				break;
			case ServerHandlerThread.address_game:
				System.out.println("Vous avez recu l'adresse pour vous connecter: " + msgPart2);
				Client.sendMsgToServer(ServerHandlerThread.ok, os);
				connectToTheGameServer(msgPart2);
				myState = CSLState.nothing;
				break;
			case ServerHandlerThread.error:
				showErrorMessage(msgPart2);
				Client.sendMsgToServer(ServerHandlerThread.ok, os);
				myState = CSLState.nothing;
				break;
			case ServerHandlerThread.create_server:
				System.out.println("Je cree le serveur" + msgPart2);
				createServer(os);
				myState = CSLState.waiting_ok;
				break;
			default:
				break;
			}
		}
		// FIN D'ATTENTE D'UN ETAT PARTICULIER
		if (myState == CSLState.nothing) {
			switch (msg) {
			// Affiche les ids des joueurs
			case ServerHandlerThread.list_available:
				Client.sendMsgToServer(Client.ok, os);
				showMessage(message, true);
				break;
			// Affiche le message en entier
			case ServerHandlerThread.your_id:
				Client.sendMsgToServer(Client.ok, os);
				showMessage(message, true);
				break;
			// Affiche le message en entier
			case ServerHandlerThread.ask_game:
				System.out.println("Voulez vous jouer avec le jouer : " + msgPart2 + "? ");
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Port : 1028 // A modifier si besoin (ou à passer en argument au lancement
	 * du client)
	 */
	private void createServer(OutputStream os) {
		System.out.println("createServer: starting");
		try {
			ServerSocket serverSocket = UtilsClient.create();
			Client.sendMsgToServer(Client.port + " " + String.valueOf(serverSocket.getLocalPort()), os);
			// Attente de connexion
			gameServerSocket = serverSocket.accept();
			System.out.println("Client connected");
			Client.myState = Client.Current_state.client_client;
			Client.myRole = current_role.server;
			Client.myPlatforme = new Platforme();
			Client.myPlatforme.show();
			if (Client.myPlatforme == null) {
				System.out.println("myPlatforme is null");
			}
			startClientClientListener(gameServerSocket, true);
			// Debut de la partie
		} catch (IOException e) {
			System.out.println(e.getMessage().toString());
			e.printStackTrace();
		}
	}

	/**
	 * Con
	 * 
	 * @param addPort
	 */
	private void connectToTheGameServer(String addPort) {
		String[] add = addPort.split(":");
		String address = add[0];
		String port = add[1];
		InetAddress intaddr;
		System.out.println("connectToTheGameServer: @:" + address + ", port:" + port);

		try {
			if(address.equals("127.0.0.1")){
				intaddr = socket.getInetAddress();
			}else{
				intaddr = InetAddress.getByName(address);
			}
			this.gameServerSocket = new Socket(intaddr, Integer.valueOf(port));
			System.out.println("connectToTheGameServer: connected");
			Client.myState = Client.Current_state.client_client;
			Client.myRole = current_role.client;
			Client.myPlatforme = new Platforme();
			// Client.myPlatforme.show();
			if (Client.myPlatforme == null) {
				System.out.println("ici null");
			}
			startClientClientListener(gameServerSocket, false);
		} catch (IOException e) {
			System.out.println("client message: " + e.getMessage().toString());
		}
	}

	private static void startClientClientListener(Socket socket, boolean isServer) {
		Client.myClientClientListener = new ClientClientListener(socket, isServer, osServ);
		new Thread(Client.myClientClientListener).start();
	}

	/**
	 * Affiche les message
	 * 
	 * @param message
	 *            : message a afficher
	 * @param isList:
	 *            true si on veux afficher la liste des joueurs
	 */
	private void showMessage(String message, boolean isList) {
		if (isList) {
			String[] parts = message.split(" ");
			if (parts.length == 2)
				System.out.println("Aucun joueur sur le serveur");
		} else {
			System.out.println(message);
		}
	}
	
	private void showErrorMessage(String msgPart2){
		if(msgPart2.compareTo("404") == 0){
			System.out.println("Le joueur n'existe pas / ou il est entrain de jouer");
		}
		if(msgPart2.compareTo("444") == 0){
			System.out.println("Vous avez mis votre ID!!!");
		}
	}

	public Socket getGameServerSocket() {
		return gameServerSocket;
	}

	public void setGameServerSocket(Socket gameServerSocket) {
		this.gameServerSocket = gameServerSocket;
	}

}
