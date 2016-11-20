package client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import client.Client.current_role;
import utils.CCLState;
import utils.ClientStateHandler;
/**
 * 
 * @author POGORELOV Mikhail et CHIEV Alexandre
 * 
 * Runnable qui gere la connexion entre le Client et le Server
 *
 */

public class ClientClientListener implements Runnable{
	
	private OutputStream osServer;
	private OutputStream os;
	private InputStream is; 
	private boolean isServer; // true si le client est le serveur de la partie
	
	private int val;
	
	private CCLState myState;
	private Socket mySocket;
	
	
	

	public CCLState getMyState() {
		return myState;
	}

	public void setMyState(CCLState myState) {
		this.myState = myState;
	}




	public ClientClientListener(Socket socket, boolean isServer, OutputStream osServer) {
		super();
		this.mySocket = socket;
		this.myState = CCLState.nothing;
		this.osServer = osServer;
		try {
			this.os = socket.getOutputStream();
			this.is = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					receiveMessage(line,os,osServer);
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
		
	
	//TODO: Implementer la fonction exit()
	/**
	 * Traite les messages reçus du second client
	 * @param message
	 * @param os
	 */
	synchronized public void receiveMessage(String message, OutputStream os, OutputStream osServer){
		String[] parts = message.split(" ");
		String msgPart2 = "";
		if(parts.length > 1){
			msgPart2 = parts[1]; 
		}
		String msg = parts[0];
		//System.out.println("ClientClientServeur: Message recu " + message + " votre etat:" + myState);
		switch (myState) {
		case in_game:
			ClientStateHandler.handle_in_game_state(msg, msgPart2, myState, val, os, osServer,mySocket);
			break;
		case nothing:
			ClientStateHandler.handle_nothing_state(msg, msgPart2, myState, val, os, isServer,mySocket);			
			break;
		case waiting_regame_server:
			ClientStateHandler.handle_waiting_regame_server(msg, msgPart2, myState, val, os, mySocket);
			break;
		case waiting_regame_client:
			ClientStateHandler.handle_waiting_regame_client(msg, msgPart2, myState, val, os, mySocket);	
			break;
		case waiting_ok:
			ClientStateHandler.handle_waiting_ok(msg, myState);			
			break;
		case waiting_ok_exit:
			ClientStateHandler.handle_waiting_ok_exit(msg,this.mySocket, osServer);
		default:
			break;
		}
	}

	
	
	
}
