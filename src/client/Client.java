package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import game.Platforme;
import utils.CCLState;
import utils.CSLState;


/**
 * 
 * @author POGORELOV Mikhail et CHIEV Alexandre
 * 
 * Remarques: l'enumeration permet de distinguer les differents niveaux de communication
 *  Pas utilis� pour cette partie
 *
 */


public class Client {

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
	public static String yourId = "YourId";
	public static String refuseGame = "RefuseGame";
	public static String error = "Error";
	public static String createServer = "CreateServer";
	
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

	public static enum Current_state {
		client_server,
		client_client
	}
	
	public static enum current_role {
		server,
		client
	}
	public static boolean isMyTurn;
	
	public static int player_num; // 1 ou 2
	
	public static Current_state myState;
	public static current_role myRole;
	
	public static Platforme myPlatforme;
	private static int val;

	public static void main(String args[]){
		System.out.println("Client started");
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
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			OutputStream os = socket.getOutputStream();
			/**On lance l'écouter qui recoit les message du serveur*/
			startClientServerListener(isr, os, socket.getInputStream());
			/** On envoi les messages ici*/
			while(true){
				String msg = scan.nextLine();
				if(msg.trim().compareTo("") == 0){
					System.out.println("Message null");
				}else{
					if(myState == Current_state.client_server){
						sendMessagesToServer(msg, os);
					}
					if(myState == Current_state.client_client){
						sendMessagesToClient(msg,myClientServerListener.getGameServerSocket().getOutputStream() );
					}
					if(msg.compareTo(Client.exit) == 0){
						socket.close();
						break;
					}
				}
			}
			System.out.println("Deconnct�");

		} catch (IOException e) {
			System.out.println("client message: " + e.getMessage().toString());
		}
	}



	private static void startClientServerListener(InputStreamReader isr, OutputStream os, InputStream is){
		myClientServerListener = new ClientServerListener(os, is);
		new Thread(myClientServerListener).start();
	}

	/**
	 * Envoi le message au serveur
	 * @param message
	 * @param os
	 */
	public static synchronized void sendMsgToServer(String message,OutputStream os){
		System.out.println("sendMsgToServer " + message);
		try {
			os.write(message.getBytes());
			os.write("\n".getBytes());
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					msg += " " + msgPart2;
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
			System.out.println("Vous vous etes reconnecte au server");
		case exit:
			myClientServerListener.myState = CSLState.waiting_ok_exit;
			sendMsgToServer(exit, os);
		case ok:
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
			if(Character.isDigit(msgPart2.charAt(0))){
				if(isMyTurn){
					int pos = Integer.valueOf(msgPart2);
					if(pos >= 0 && pos <= 8){
						val = Client.player_num == 2? 2:1;
						if(myPlatforme.put(Integer.valueOf(msgPart2), val) == true){
							myPlatforme.show();
							//myPlatforme.show2();
//							String resp = GameHandler.checkPlatform(Client.myPlatforme);
//							if(resp != null){
//								if(resp.compareTo(Client.youWin) == 0 || resp.compareTo(Client.draw) == 0){
//									//myClientClientListener.setMyState(CCLState.waiting_ok);
//								}
//							}
							sendMsgToServer(message, os);
							isMyTurn = false;
						}else{
							System.out.println("probleme");
						}
					}else{
						System.out.println("position impossible");
						break;
					}
				}else{
					System.out.println("C'est pas votre tour!");
				}
			}
			break;
		case exit:
			System.out.println("Vous quittez la partie");
			Client.myRole = Client.current_role.client;
			Client.myState = Client.Current_state.client_server;
		case regame:
			System.out.println("Vous avez voulez refaire une partie");
			if(myClientClientListener.getMyState() == CCLState.nothing){
				myPlatforme.refresh();
				sendMsgToServer(regame, os);
			}else{
				System.out.println("Vous ne pouvez pas faire cette demande maintenant!");
			}
			
			break;
		default:
			System.out.println("sendMessagesToClient: reponse n'est pas conforme au protocole:");
			break;
		}	
	}

	private void exit(){

	}






}
