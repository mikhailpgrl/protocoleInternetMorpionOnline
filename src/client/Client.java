package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import game.Platforme;
import javafx.application.Application;
import javafx.stage.Stage;
import utils.CCLState;
import utils.CSLState;


/**
 * 
 * @authors POGORELOV Mikhail et CHIEV Alexandre
 * Client:
 * 	Gere la connection au serveur (local/distant)
 * 	Une fois connecté au serveur, lance un thread gerant les entrées depuis le serveur
 *  Traite les messages envoyés au server et au client
 */


public class Client{

	// Messages partant de client au server
	public static final String askList = "AskList"; // ok
	public static final String play = "Play"; // Concatené à l'id du seconde joueur
	public static final String answer_play = "Answer"; // Concatené à Y ou N
	public static final String adressGame = "AddressGame"; // Concatené à l'addresse du serveur crée
	public static final String port = "Port";
	public static final String returnServer = "ReturnServer";
	public static final String exit = "Exit";
	public static final String ok = "OK";
	public static final String askId = "AskId";

	// Réponses possibles du serveur
	public static final String yourId = "YourId";
	public static final String refuseGame = "RefuseGame";
	public static final String error = "Error";
	public static final String createServer = "CreateServer";
	
	// Message partant de client au client
	public static final String youStartGame = "YouStartGame"; // indique au cc qu'il commence
	public static final String pos = "Pos"; // + INT envoi le coup effectue dans la case Int (0-8)
	public static final String youWin = "YouWin"; // Previen l'adversaire qu'il gagne
	public static final String draw = "Draw"; // Previent d'une égalité
	public static final String regame = "Regame";// Demande au cc s'il veux refaire une partie
	public static final String yes = "Yes"; // réponse a Regame
	// Error + 777 si l'autre client a triché

	
	private static ClientServerListener myClientServerListener;
	public static ClientClientListener myClientClientListener;

	/**
	 * Etat courant du client: 
	 *  1: En communication avec le serveur "client_server"
	 *  2: En communication avec le client "client_client"
	 */
	public static enum Current_state {
		client_server,
		client_client
	}
	
	/**
	 * Rôle du client lorsqu'il est connecté à un client:
	 * 	1: s'il est serveur "server"
	 *  2: s'il est client  "client"
	 */
	public static enum current_role {
		server,
		client
	}
	
	public static boolean isRunning; // True si le client tourne, False sinon
	public static boolean isMyTurn; // True si c'est à mon tour, False sinon
	
	public static int player_num; // 1 ou 2
	
	public static Current_state myState; // Etat courant du client
	public static current_role myRole; // Role courant du client
	
	public static Platforme myPlatforme; // Plateforme de jeu
	private static int val;

	private static OutputStream osServer;
	
	
	public static void main(String args[]){
		System.out.println("Client started");
		//Affichons les commandes
		explainCommands();
		System.out.println("Veuillez saisir l'addresse IP du serveur/entrée pour localhost");
		Scanner scan = new Scanner(System.in);
		String address = scan.nextLine();
		if(address != null){
			if(address.compareTo("") == 0){
				address = "localhost";
			}
		}
		
		try (Socket socket = new Socket(address, 1027);){
			System.out.println("Connecté au serveur");		
			myState = Current_state.client_server;
			myRole = current_role.client;
			isMyTurn = false;
			osServer = socket.getOutputStream();
			isRunning = true;
			
			/**On lance la fonction qui permet de gerer le signal de sortie*/
			startCtrl_CHandler(socket);
			/**On lance l'écouter qui recoit les message du serveur*/
			startClientServerListener(socket);
			
			/** On envoi les messages ici*/
			while(isRunning){
				String msg = scan.nextLine();
				if(isRunning){
					if(msg.trim().compareTo("") == 0){
						System.out.println("Message null");
					}else{
						if(myState == Current_state.client_server){
							sendMessagesToServer(msg, osServer);
						}
						if(myState == Current_state.client_client){
							sendMessagesToClient(msg,myClientServerListener.getGameServerSocket().getOutputStream() );
						}
					}	
				}
			}
		} catch (IOException e) {
			System.out.println("client message: " + e.getMessage().toString());
		}
		finally {
			System.out.println("Vous avez été déconnecté!");
			scan.close();
		}
	}


	/**
	 * Lance l'écouteur de la socket client serveur
	 * @param socket
	 */
	private static void startClientServerListener(Socket socket){
		myClientServerListener = new ClientServerListener(socket);
		new Thread(myClientServerListener).start();
	}

	/**
	 * Envoi le message au serveur
	 * @param message
	 * @param os
	 */
	public static synchronized void sendMsgToServer(String message,OutputStream os){
//		System.out.println("sendMsgToServer " + message);
		try {
			os.write(message.getBytes());
			os.write("\n".getBytes());
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(e.getMessage().toString().equals("Broken pipe")){
				System.out.println("Le server / client est déjà déconnecté!");
			}else{
				System.out.println(e.getMessage().toString());
			}
		}
	}


	/**
	 *  Verifie le message tap� avant de l'envoyer
	 * @param message : message envoyé par le client
	 * @param os: outputstream utilisé
	 */

	public static void sendMessagesToServer(String message, OutputStream os){
		String[] parts = message.split(" ");
		String msgPart2 = "";
		if(parts.length > 1){
			msgPart2 = parts[1]; 
		}
		String msg = parts[0];
		
		System.out.println("sendMessages: Sending " + message);

		switch (msg) {
		case askList:
			myClientServerListener.myState = CSLState.nothing;
			sendMsgToServer(msg, os);
			break;
		case play:
			myClientServerListener.myState = CSLState.waiting_refuse_error_create_addr;
			msg+= " " +msgPart2;
			sendMsgToServer(msg, os);
			break;
		case answer_play:
			if(msgPart2.compareTo("") != 0){
				if((msgPart2.toLowerCase().compareTo("Y".toLowerCase()) == 0) || (msgPart2.toLowerCase().compareTo("N".toLowerCase()) == 0)){
					msg += " " + msgPart2.toUpperCase();
					myClientServerListener.myState = CSLState.waiting_error_create_server;
					sendMsgToServer(msg, os);	
				}else{
					System.out.println("SendMessges: reponse n'est pas conforme au protocole:" + msg + " " + msgPart2);
				}	
			}else{
				System.out.println("SendMessges: veuillez preciser la reponse (answer Y/N)!" );
			}

			break;
		case port:
			sendMsgToServer(message, os);
			myClientServerListener.myState = CSLState.waiting_ok;
			break;
		case returnServer:
			myClientServerListener.myState = CSLState.waiting_ok;
			sendMsgToServer(returnServer, os);
			System.out.println("Vous vous etes reconnecte au server");
			break;
		case exit:
			System.out.println("Vous quittez le server");
			myClientServerListener.myState = CSLState.waiting_ok_exit;
			sendMsgToServer(exit, os);
			break;
		case ok:
			break;
		case askId:
			myClientServerListener.myState = CSLState.waiting_yourid;
			sendMsgToServer(msg, os);
			break;
		default:
			System.out.println("SendMessges: reponse n'est pas conforme au protocole:");
			break;
		}
	}
	
	/**
	 * Verifie les messages avant de les envoyer au second client
	 * @param message
	 * @param os
	 */
	public static void sendMessagesToClient(String message, OutputStream os){
		String[] parts = message.split(" ");
		String msgPart2 = "";
		if(parts.length > 1){
			msgPart2 = parts[1]; 
		}
		String msg = parts[0];
		System.out.println(msg);
		System.out.println("sendMessagesToClient: Sending " + message);
		switch (msg) {
		case youStartGame:
			myClientClientListener.setMyState(CCLState.in_game);
			sendMsgToServer(msg, os);
			break;
		case pos:
			if(!msgPart2.equals("")){
				if(Character.isDigit(msgPart2.charAt(0))){
					if(myClientClientListener.getMyState() == CCLState.waiting_regame_server){
						myClientClientListener.setMyState(CCLState.in_game);
						isMyTurn = true;					
					}
					if(isMyTurn){
						int pos = Integer.valueOf(msgPart2);
						if(pos >= 0 && pos <= 8){
							val = Client.player_num == 2? 2:1;
							if(myPlatforme.put(Integer.valueOf(msgPart2), val) == true){
								myPlatforme.show();
								System.out.println("etat "  + myClientClientListener.getMyState().toString());
								sendMsgToServer(message, os);
								isMyTurn = false;
							}else{
								System.out.println("Position déjà prise!!!");
							}
						}else{
							System.out.println("Position impossible!!!");
							break;
						}
					}else{
						System.out.println("C'est pas votre tour!");
					}
				}
			}else{
				System.out.println("Vous n'avez pas précisé la position!!!");
			}
			break;
		case exit:
			System.out.println("Vous quittez la partie");
			Client.myState = Client.Current_state.client_server;
			Client.myRole = Client.current_role.client;
			myClientClientListener.setMyState(CCLState.waiting_ok_exit);
			sendMsgToServer(exit, os);
			//sendMessagesToServer(Client.returnServer, os);
			
			break;
		case regame:
			System.out.println("Vous avez demandé de refaire une partie");
			if(myClientClientListener.getMyState() == CCLState.nothing){
				if(myRole == current_role.server){
					myClientClientListener.setMyState(CCLState.waiting_regame_server);
				}else{
					myClientClientListener.setMyState(CCLState.waiting_regame_client);
				}
				myPlatforme.refresh();
				sendMsgToServer(regame, os);
			}else{
				System.out.println("Vous ne pouvez pas faire cette demande maintenant!");
			}
			break;
		case yes:
			sendMsgToServer(message, os);
			break;
		case ok:
			sendMsgToServer(ok, os);
			break;
		default:
			System.out.println("sendMessagesToClient: reponse n'est pas conforme au protocole:");
			break;
		}	
	}
	
	/**
	 * Methode affichant les commandes possibles du client
	 */
	private static void explainCommands(){
		System.out.println("'AskId' = demander votre id");
		System.out.println("'AskList' = demander la liste des joueurs connectés au serveur");
		System.out.println("'Play + id' = jouer avec le joueur id");
		System.out.println("'Answer Y/N' = accepter ou refuser la partie");
		System.out.println("'Pos + int ' = jouer à son tour");
		System.out.println("'Regame' = demander de refaire la partie");
		System.out.println("'Exit' = quitter le jeu / partie");
	}
	
	
	/**
	 * Fonction permettant de gerer la sortie de programme (Control + C)
	 * On envoi un message EXIT vers le serveur et/ou client en fonction de l'état actuel
	 * @param socket
	 */
	public static void startCtrl_CHandler(Socket socket){
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				if(myState == Current_state.client_server){
					try {
						sendMessagesToServer(Client.exit, socket.getOutputStream());
					} catch (IOException e) {
						if(e.getMessage().toString().equals("Broken pipe")){
							System.out.println("Le serveur est offline");
						}else{
							System.out.println(e.getMessage().toString());
						}
					}
				}
				if(myState == Current_state.client_client){
					try {
						sendMessagesToClient(Client.exit, myClientClientListener.getMySocket().getOutputStream());
						sendMessagesToServer(Client.exit, socket.getOutputStream());
					} catch (IOException e) {
						if(e.getMessage().toString().equals("Broken pipe")){
							System.out.println("Le serveur ou serveur est déjà offline");
						}else{
							System.out.println(e.getMessage().toString());
						}
					}
				}
			}
		});
	}

}
