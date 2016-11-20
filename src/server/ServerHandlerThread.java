package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import client.Client;
import client.ClientModel;
import utils.SHState;
import utils.UtilsServer;


public class ServerHandlerThread implements Runnable{



	/* Les messages partant du serveur*/
	public static final String list_available = "List"; // Contatené aux ids des joueurs
	public static final String error = "Error";
	public static final String ask_game = "AskGame";
	public static final String refuse_game = "RefuseGame";
	public static final String create_server = "CreateServer";
	public static final String adress_game = "AdressGame";
	public static final String port = "Port";
	public static final String ok = "OK";
	public static final String your_id = "YourId";

	// Port qui sera utilisé pour le jeu
	private int numPort;
	private String add;

	private ClientModel myClientModel;
	private ClientModel myClientModel2; // avec qui jouer
	

	
	public static SHState myState;
	

	public ServerHandlerThread(ClientModel cm) {
		super();
		this.myState = SHState.nothing;
		this.myClientModel = cm;
		this.myClientModel2 = null;
	}



	@Override
	public void run() {
		BufferedReader myIsr;
		try {
			myIsr = new BufferedReader(new InputStreamReader(myClientModel.getSocket().getInputStream()));
			/**
			 * Message welcome (id + list)
			 */
			String msgTmp = your_id;
			msgTmp += " "+myClientModel.getId();
			System.out.println("Server answer: " + msgTmp);
			sendMsgToClient(msgTmp, myClientModel.getSocket().getOutputStream());

			String list = getPlayersList();
			System.out.println("List est = " + list);
			sendMsgToClient(list, myClientModel.getSocket().getOutputStream());
			/**
			 * Fin envoi message welcome
			 */
			//while (Utils.isConnected(myClientModel.getId())){
				String line;
				while((line = myIsr.readLine()) != null) {
					System.out.println(line);
					receiveMessage(line, myClientModel.getSocket().getOutputStream());
				}
		//	}
		}
		catch (IOException e1) {
			System.out.println(e1.getMessage().toString());
			System.out.println("Le client "+ myClientModel.getId() + " @" + myClientModel.getAddress().toString() + " vient de se d�connecter!");
			//e1.printStackTrace();
		}
	}

	/**
	 *  Envoi les messages via outputstream 
	 * @param message : message a envoyer
	 * @param os 	  : outputstream
	 */
	public synchronized void sendMsgToClient(String message,OutputStream os){
		try {
			os.write(message.getBytes());
			os.write("\n".getBytes());
			os.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage().toString());
			e.printStackTrace();
		}
	}

	/**
	 * Verifie le message avant de l'envoyer
	 * @param message : message à envoyer
	 * @param os      : outputstream à utiliser
	 * Remarque: Verifies le message avant de l'envoyer
	 */
	public  void sendMessage(String message, OutputStream os){
		String[] parts = message.split(" ");
		String msgPart2 = "";
		if(parts.length > 1){
			msgPart2 = parts[1]; 
		}
		String msg = parts[0];
		
		switch (msg) {
		case list_available:
			this.myState = SHState.waiting_ok;
			System.out.println("sendMessage: case list_available ");
			String list = getPlayersList();
			System.out.println("List est = " + list);
			myState = SHState.waiting_ok;
			sendMsgToClient(list, os);
			break;
		case ask_game:
			if(isIdExist(msgPart2)){
				sendMsgToClient(message, os);
			}else{
				String res = error;
				res += " 404: le jouer n'existe pas";
				sendMsgToClient(res, os);
			}
			break;
		case refuse_game:
			//System.out.println("je suis dans refuse game");
			this.myState = SHState.waiting_ok;
			sendMsgToClient(message, os);
			break;
		case create_server:
			this.myState = SHState.waiting_port;
			sendMsgToClient(message, os);
			break;
		case adress_game:
			sendMsgToClient(message, os);
			break;
		case ok:
			sendMsgToClient(message, os);
			break;
		case your_id:
			break;
		case error:
			sendMsgToClient(message, os);
			break;
		default:
			System.out.println("SendMessages: bad response: message does not respect the protocol!");
			break;
		}
	}


	/**
	 * Envoi true si le joueur existe
	 * @param id : l'id du joueur
	 * @return
	 */
	@SuppressWarnings("unused")
	private static boolean isIdExist(String id){
		for (ClientModel cm: Server.clients) {
			String key = cm.getId();
			if(id.compareTo(key) == 0){
				return true;
			}
		}
		return false;
	}

	/**
	 * Renvoi la liste des joueurs
	 * @return
	 */
	private String getPlayersList(){
		String res = list_available + " ";
		for (ClientModel cm : Server.clients) {
			if(myClientModel.getId().compareTo(cm.getId()) != 0){
				res += cm.getId();
				res += " ";	
			}
		}
		res+="EndList";
		//System.out.println("getPlayersList: " + res);
		return res;
	}
	/**
	 * Traite les message recu
	 * @param message : message recu
	 * @param os : outputstream dans lequel on renvoie la reponse
	 * @throws IOException
	 */
	private synchronized void receiveMessage(String message, OutputStream os) throws IOException{
		long threadId = Thread.currentThread().getId();
		String[] parts = message.split(" ");
		String msgPart2 = "";
		if(parts.length > 1){
			msgPart2 = parts[1]; 
		}
		String msg = parts[0];

		System.out.println(threadId + "receiveMessage:" + message + " state = "  + myState.toString());
		//UtilsServer.showQueue();
		
		if(this.myState == SHState.waiting_ok){
			if(msg.compareTo(Client.ok) == 0){
				System.out.println(threadId + "in state waiting_ok completed");
				myState = SHState.nothing;
			}else{
				System.out.println(threadId + "J'attend la reponse 'ok' du client!");
			}
		}
		if(this.myState == SHState.waiting_port){
			if(msg.compareTo(Client.port) == 0){
				System.out.println(threadId + "in state waiting_port completed");
				myState = SHState.nothing;
			}else{
				System.out.println(threadId + "J'attend la reponse 'port' du client!");
			}
		}
		if(this.myState == SHState.waiting_answer){
			if(msg.compareTo(Client.answer_play) == 0){
				/** On traite deux cas:
				 * - Le joueur avait accept� la partie
				 * - Le joeur a refuse la partie*/

				System.out.println("answser_play = "  + message);

				/** Il accepte la partie et va creer le serveur*/
				if(msgPart2.compareTo("Y") == 0){
					System.out.println("Le client a accepté la partie");
					// Le client accepte la partie donc il va creer le serveur
					sendMsgToClient(create_server, os);
					System.out.println(threadId + "in state waiting_answer completed");
					System.out.println(threadId + "new state waiting_port");
					myState = SHState.waiting_port;
				}else{
					/**Il refuse la partie*/
					if(msgPart2.compareTo("N") == 0){
						sendMsgToClient(ok, os);
						System.out.println(threadId + "Le client a refusé la partie");
						/**Envoi la reponse negative au client qui avait demande la partie*/
						ClientModel asker = UtilsServer.getAskerFromQueue(myClientModel.getId());
						sendMsgToClient(refuse_game, asker.getMySocket().getOutputStream());
						UtilsServer.removeFromQueue(asker.getId(), myClientModel.getId());
						myClientModel2 = null;
						myState = SHState.nothing;
						System.out.println(threadId + "in state waiting_answer completed");
					}else{
						System.out.println(threadId + "1) J'attend la reponse 'Answer Y/N' du client!");
					}
				}
				
			}else{
				System.out.println(threadId + "2) J'attend la reponse 'Answer Y/N' du client!");
			}
		}
		if(this.myState == SHState.nothing){
			switch (msg) {
			case Client.exit:
				System.out.println(threadId + "Le client + " + myClientModel.getId() + " @"+myClientModel.getSocket().getInetAddress().toString() + 
						" veut se d�connecter!");
				UtilsServer.removeClientFromList(myClientModel.getId());
				sendMessage(ServerHandlerThread.ok, os);
				myClientModel.getMySocket().close();
				break;
			case Client.askList:
				System.out.println(threadId + "client asked a list");
				sendMessage(list_available, os);
				break;
			case Client.play:
				System.out.println(threadId + "Demande de jouer avec id + " + msgPart2);

				if(msgPart2.compareTo(myClientModel.getId()) == 0){
					String rep = error+ " 404";
					sendMessage(rep, os);
					myState = SHState.waiting_ok;
				}else{
					// On verifie si le second joueur est dans le jeu
					if(!UtilsServer.isInGame(msgPart2)){
						/**
						 * Celui qui demande fait le client, et celui qui re�oit fait le serveur
						 */
//						if(myClientModel2 == null){
							// On recupere son model grâce à son ID
							myClientModel2 = UtilsServer.getClientById(msgPart2);
							/**
							 * On demande a cm2 s'il veut bien faire la partie
							 */
							String tmpMsg = ask_game;
							// Type du Message AskGame + ID
							tmpMsg += " "+ myClientModel.getId();
							if(myClientModel2 != null){
								System.out.println(threadId + "second client is not null");
							}
							// On met les jouers dans la file d'attente
							UtilsServer.addToQueue(myClientModel.getId(), myClientModel2.getId());
							sendMsgToClient(tmpMsg, myClientModel2.getSocket().getOutputStream());
							myState = SHState.waiting_answer;
//						}else{
//							System.out.println(threadId + "myClientModel2 is not null = " + msgPart2);
//						}

					}else{
						System.out.println(threadId + "Le joueur " + msgPart2 + " est deja dans le jeu");
						String rep = error+ " 404";
						sendMessage(rep, os);
						myState = SHState.waiting_ok;
						
					}
				}

				break;
				// Le client renvoi l'addres donc c'est le serveur	
			case Client.adressGame:
				System.out.println(threadId + "Server adresse recue: " + msgPart2);
				String[] add = msgPart2.split(":");
				String address = add[0];
				this.add = address;
				//String port = add[1];
				// asker va recevoir l'@ a laquelle se connecter
				break;
			case Client.port:
				System.out.println("Recu le port du client:" + msgPart2);
				this.numPort = Integer.valueOf(msgPart2);
				myState = SHState.nothing;
				ClientModel asker = UtilsServer.getAskerFromQueue(myClientModel.getId());
				ClientModel resp = UtilsServer.getResponderFromQueue(asker.getId());
				UtilsServer.setClientInGame(asker);
				UtilsServer.setClientInGame(resp);
				sendMsgToClient(adress_game + " " +resp.getSocket().getInetAddress().getHostAddress() +":"+numPort,asker.getSocket().getOutputStream() );
				UtilsServer.removeFromQueue(asker.getId(), myClientModel.getId());
				myClientModel2 = null;
				break;
			case Client.askId:
				myState = SHState.waiting_ok;
				String msgTmp = your_id;
				msgTmp += " "+myClientModel.getId();
				System.out.println(threadId + "Server answer: " + msgTmp);
				sendMsgToClient(msgTmp, os);
				break;
			case Client.returnServer:
				UtilsServer.unsetClientInGame(myClientModel);
				sendMsgToClient(ok, os);
				System.out.println("Le client " + myClientModel.getId() + "@" + myClientModel.getMySocket().getInetAddress().getHostAddress()+ " vient de se reconnecter!");
				break;
			default:
				break;
			}
		}
		

		
	}


}
