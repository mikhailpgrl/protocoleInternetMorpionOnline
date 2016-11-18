package client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import client.Client.current_role;
import client.ClientServerListener.ClientSL_current_state;
import game.GameHandler;
/**
 * 
 * @author POGORELOV Mikhail et CHIEV Alexandre
 * 
 * Runnable qui gere la connexion entre le Client et le Server
 *
 */

public class ClientClientListener implements Runnable{

	private OutputStream os;
	private InputStream is; 
	private boolean isServer; // true si le client est le serveur de la partie
	
	private int val;
	
	private CCL_current_state myState;
	public static enum CCL_current_state {
		nothing,
		in_game, //Pos, Win, Lose, Draw, Exit
		waiting_ok
	}
	
	

	public CCL_current_state getMyState() {
		return myState;
	}

	public void setMyState(CCL_current_state myState) {
		this.myState = myState;
	}




	public ClientClientListener(OutputStream os, InputStream is, boolean isServer) {
		super();
		this.myState = CCL_current_state.nothing;
		this.os = os;
		this.is = is;
		this.isServer = isServer;
	}




	@Override
	public void run() {
		BufferedReader myIsr;
		if(Client.myRole == current_role.server){
			Client.isMyTurn = false;
			Client.player_num = 2;
			Client.sendMessagesToClient(Client.youStartGame, os);
		}
		try {
			myIsr = new BufferedReader(new InputStreamReader(is));
			char[] buf = new char[1024];;
			while (true){
				String line;
				while((line = myIsr.readLine()) != null) {
					receiveMessage(line,os);
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
		
	
	// TODO: Gerer les etat pour la communication client-client
	//TODO: Implementer la fonction exit()
	/**
	 * Traite les messages reçus du second client
	 * @param message
	 * @param os
	 */
	synchronized public void receiveMessage(String message, OutputStream os){
		String[] parts = message.split(" ");
		String msgPart2 = "";
		if(parts.length > 1){
			msgPart2 = parts[1]; 
		}
		String msg = parts[0];
		System.out.println("ClientClientServeur: Message recu " + message + " votre etat:" + myState);
		if(myState == ClientClientListener.CCL_current_state.in_game){
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
						myState = ClientClientListener.CCL_current_state.nothing;
						System.out.println("Votre adversaire gagne la partie!");
					}
					if(resp.compareTo(Client.draw) == 0){
						myState = ClientClientListener.CCL_current_state.nothing;
						Client.sendMsgToServer(Client.draw, os);
						System.out.println("Egalité!");
					}
					Client.sendMsgToServer(resp, os);
				}else{
					Client.isMyTurn = true;
				}
				break;
			case	Client.youWin:
				myState = ClientClientListener.CCL_current_state.nothing;
				Client.sendMsgToServer(Client.ok, os);
				System.out.println("Vous avez gagné la partie!");
				break;
			case	Client.draw:
				Client.sendMsgToServer(Client.ok, os);
				System.out.println("Egalité");
				break;
			case Client.exit:
				System.out.println("Le client a quitte la partie");
				break;
			default:
				break;
			}
		}
		if(myState == ClientClientListener.CCL_current_state.nothing){
			switch (msg) {
			case Client.youStartGame:
				myState = ClientClientListener.CCL_current_state.in_game;
				System.out.println("Vous commencez la partie");
				Client.myPlatforme.show();
				Client.player_num = 1;
				Client.isMyTurn = true;
				break;
			case Client.regame:
				myState = ClientClientListener.CCL_current_state.in_game;
				System.out.println("La partie va recommencer!");
				Client.myPlatforme.refresh();
				Client.myPlatforme.show();
				Client.sendMsgToServer(Client.youStartGame, os);
				break;
			case Client.exit:
				System.out.println("Le client a quitte la partie");
				break;
			default:
				break;
			}
		}
		if(myState == ClientClientListener.CCL_current_state.waiting_ok){
			if(msg.compareTo(Client.ok) == 0){
				myState = ClientClientListener.CCL_current_state.nothing;
			}else{
				System.out.println("un autre etat");
			}
		}
		
	}
	
	
	
	
}
