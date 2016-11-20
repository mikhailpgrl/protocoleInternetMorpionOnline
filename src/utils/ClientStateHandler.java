package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import client.Client;
import client.Client.Current_state;
import client.Client.current_role;
import game.GameHandler;

public class ClientStateHandler {

	public static void handle_in_game_state(String msg, String msgPart2, CCLState myState, int val, OutputStream os, OutputStream osSever, Socket mySocket){
		switch (msg) {
		case Client.pos:
			System.out.println("Joueur: joue + " + msgPart2);
			// On inverse car on recoi les messages
			char c = (Client.player_num == 2? 'X': '0');
			System.out.println("Vous jouez avec " + c);
			val = Client.player_num == 2? 1:2;
			Client.myPlatforme.put(Integer.valueOf(msgPart2), val);
			Client.myPlatforme.show();
			//Client.myPlatforme.show2();
			String resp = GameHandler.checkPlatform(Client.myPlatforme);
			if(resp != null){
				if(resp.compareTo(Client.youWin) == 0){
					Client.myClientClientListener.setMyState(CCLState.waiting_ok);
					Client.sendMsgToServer(Client.youWin, os);
					System.out.println("Votre adversaire gagne la partie!");
				}else{
					if(resp.compareTo(Client.draw) == 0){
						Client.myClientClientListener.setMyState(CCLState.waiting_ok);
						Client.sendMsgToServer(Client.draw, os);
						System.out.println("Egalité!");
					}
				}
				Client.sendMsgToServer(resp, os);
			}else{
				Client.isMyTurn = true;
			}
			break;
		case	Client.youWin:
			Client.sendMsgToServer(Client.ok, os);
			System.out.println("Vous avez gagné la partie!");
			Client.myClientClientListener.setMyState(CCLState.nothing);
			break;
		case	Client.draw:
			Client.sendMsgToServer(Client.ok, os);
			System.out.println("Egalité");
			break;
		case Client.exit:
			System.out.println("Votre adversaire client a quitte la partie");
			Client.myClientClientListener.setMyState(CCLState.nothing);
			Client.myRole =current_role.client;
			Client.myState = Current_state.client_server;
			// J'envoi ok a mon adversaire 
			Client.sendMessagesToClient(Client.ok, os);
			Client.sendMessagesToServer(Client.returnServer, osSever);
			System.out.println("Vous vous reconnectez au server");
			try {
				mySocket.close();
			} catch (IOException e) {
				System.out.println("Vous vous reconnectez au server");
				//e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	
	public static void handle_nothing_state(String msg, String msgPart2, CCLState myState, int val, OutputStream os, boolean isServer, Socket socket){

		switch (msg) {
		case Client.youStartGame:
			Client.myClientClientListener.setMyState(CCLState.in_game); 
			System.out.println("Vous commencez la partie! (" + myState.toString() + ")" );
			Client.myPlatforme.show();
			Client.player_num = 1;
			Client.isMyTurn = true;
			break;
			// TODO: traiter le message regame
		case Client.regame:
			Client.myPlatforme.refresh();
			if(isServer){
				Client.myClientClientListener.setMyState(CCLState.waiting_regame_server); // Je réponds YouStartGame ou Pos ou Exit
				System.out.println("Votre ennemie vous demande de rejouer, voulez-vous recommencer? (YouStart / Pos /Exit");
				//Client.myPlatforme.show();
			}else{
				Client.myClientClientListener.setMyState(CCLState.waiting_regame_client); // Je répond Yes ou Exit
				System.out.println("Votre ennemie vous demande de rejouer, voulez-vous recommencer? (Yes / Exit");
			}
			
			
//			Client.myPlatforme.refresh();
//			Client.myPlatforme.show();
//			Client.sendMsgToServer(Client.youStartGame, os);
			break;
		case Client.error:
			System.out.println(msg);
			break;
		case Client.exit:
			try {
				socket.close();
				System.out.println("Le client a quitte la partie");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	public static void handle_waiting_regame_server(String msg, String msgPart2, CCLState myState, int val, OutputStream os, Socket mySocket){
		/**
		 * Messages possibles dans cet etat
		 * 1: Yes 2: Exit
		 * 
		 */
		switch (msg) {
		/**
		 * Si le client répond Yes : alors YouStartGame ou Pos est attendu
		 */
		case Client.yes:
			// Accepte de refaire la partie
			// On relance le jeu: on dit YouStartGame ou Pos directement
			/**
			 * Dans notre cas on dira YouStartGame
			 */
			Client.sendMessagesToClient(Client.youStartGame, os);
			Client.myPlatforme.show();
			Client.myClientClientListener.setMyState(CCLState.in_game);
			break;
		case Client.exit:
			Client.sendMessagesToClient(Client.ok, os);
			// On quitte la partie
			try {
				System.out.println("Reconnexion au serveur");
				mySocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Abandon
		default:
			System.out.println("Mauvais message recu: attente d'un 'Yes' ou d'un 'Exit");
			break;
		}
	}
	
	public static void handle_waiting_regame_client(String msg, String msgPart2, CCLState myState, int val, OutputStream os, Socket mySocket){
		/**
		 * Messages possibles dans cet état
		 * 1: YouStartGame 2: Pos 3: Exit
		 */
		switch (msg) {
		case Client.youStartGame:
			System.out.println("Vous commencez la partie");
			Client.myPlatforme.show();
			Client.myClientClientListener.setMyState(CCLState.in_game);
			Client.isMyTurn = true;
			break;
		case Client.pos:
			System.out.println("Joueur: joue + " + msgPart2);
			// On inverse car on recoi les messages
			char c = (Client.player_num == 2? 'X': '0');
			System.out.println("Vous jouez avec " + c);
			val = Client.player_num == 2? 1:2;
			Client.myPlatforme.put(Integer.valueOf(msgPart2), val);
			Client.myClientClientListener.setMyState(CCLState.in_game);
			Client.myPlatforme.show();
			Client.isMyTurn = true;
			break;
		case Client.exit:
			Client.sendMessagesToClient(Client.ok, os);
			// On quitte la partie
			try {
				System.out.println("Reconnexion au serveur");
				mySocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			System.out.println("Mauvais message recu: attente d'un 'YouStart', d'un 'Pos' ou d'un'Exit");
			break;
		}
	}

	public static void handle_waiting_ok(String msg, CCLState myState){
		if(msg.compareTo(Client.ok) == 0){
			Client.myClientClientListener.setMyState(CCLState.nothing);
		}else{
			System.out.println("un autre etat");
		}
	}
	
	public static void handle_waiting_ok_exit(String msg, OutputStream osServer){
		if(msg.compareTo(Client.ok) == 0){
			Client.myClientClientListener.setMyState(CCLState.nothing);
			Client.sendMessagesToServer(Client.returnServer, osServer);
		}else{
			System.out.println("un autre etat");
		}
	}
	
}
