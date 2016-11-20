package utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import client.ClientModel;
import server.Server;

public class UtilsServer {

	
	/**
	 * Renvoi true si le client est toujours dans la liste (s'il est connect�)
	 * @param id
	 * @return
	 */

	synchronized public static boolean isConnected(String id){
		for (ClientModel cm : Server.clients) {
			if(cm.getId().compareTo(id) == 0){
				return true;
			}
		}
		return false;
	}
	
	synchronized static public void setClientInGame(ClientModel myClientModel){
		for (ClientModel cm : Server.clients) {
			if(cm.getId().compareTo(myClientModel.getId()) == 0){
				cm.setInGame(true);
			}
		}
	}
	synchronized public static void unsetClientInGame(ClientModel myClientModel){
		for (ClientModel cm : Server.clients) {
			if(cm.getId().compareTo(myClientModel.getId()) == 0){
				cm.setInGame(false);
			}
		}
	}
	
	

	/**
	 * Enleve le client de la liste
	 * @param id : id du client
	 */
	synchronized public static void removeClientFromList(String id){
		for (ClientModel cm : Server.clients) {
			if(cm.getId().compareTo(id) == 0){
				Server.clients.remove(cm);
				break;
			}
		}
	}
	
	/**
	 * Enleve le couple de la file d'attente
	 * @param id1 demande la partie
	 * @param id2 accepte/refuse la partie
	 */
	synchronized public static void removeFromQueue(String id1, String id2){
		for(Iterator<Map.Entry<String, String>> it = Server.queue.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			if(entry.getKey().compareTo(id1) == 0) {
				System.out.println("Enleve de la file d'attente");
				it.remove();
				break;
			}
		}
	}
	
	synchronized public static void showQueue(){
		for(Iterator<Map.Entry<String, String>> it = Server.queue.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
	
	
	/**
	 * Renvoie le model du joueur 2
	 * @param id1 
	 * @return
	 */
	synchronized public static ClientModel getResponderFromQueue(String id1){
		String res = "";
		for(Entry<String, String> entry : Server.queue.entrySet()) {
			System.out.println("getResponderFromQueue:" +  entry.getKey().toString());
			if(entry.getKey().compareTo(id1) == 0){
				res = entry.getValue();
				//System.out.println("getResponderFromQueue: accepte de jouer avec " + res );
				return getClientById(res);
			}
		}
		return null;
	}
	
	/**
	 * Renvoie le model du joueur 1
	 * @param id2
	 * @return
	 */
	synchronized public static ClientModel getAskerFromQueue(String id2){
		String res = "";
		for(Entry<String, String> entry : Server.queue.entrySet()) {
			System.out.println("getAskerFromQueue:" +  entry.getKey().toString());
			if(entry.getValue().compareTo(id2) == 0){
				res = entry.getKey();
				//System.out.println("getAskerFromQueue: accepte de jouer avec " + res );
				return getClientById(res);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param id1 demande la partie
	 * @param id2 accepte / refuse la partie
	 */
	synchronized public static void addToQueue(String id1, String id2){
		System.out.println("addToQueue:" + id1 + " " + id2 + " ajoute dans la file d'attente");
		Server.queue.put(id1, id2);
	}
	
	/**
	 * 
	 * @param id1 : le joueur qui demande � id2 de jouer la partie
	 * @param id2 : le joueur 2
	 */
	synchronized public boolean isInQueue(String id1, String id2){
		// Si le joueur a deja demander la partie, il ne peut plus redemander
		for(Entry<String, String> entry : Server.queue.entrySet()) {
			if(entry.getKey().compareTo(id1) == 0){
				System.out.println("addPlayerToAskGame: Demande de partie");
				return true;
			}
		}
		return false;
	}

	synchronized public static Boolean isInGame(String id){
		for (ClientModel cm : Server.clients) {
			System.out.println("id = " + cm.getId() + " id = " + id);
			if(cm.getId().compareTo(id) == 0){
				System.out.println("ici");
				if(cm.isInGame()){
					return true;
				}else{
					System.out.println("isInGame: le joueur peut jouer");
					return false;
				}
			}
		}
		System.out.println("Le joueur demand� n'existe pas");
		return true;
	}

	synchronized public static ClientModel getClientById(String id){
		for (ClientModel cm : Server.clients) {
			if(cm.getId().compareTo(id) == 0){
				return cm;
			}
		}
		return null;
	}

	
}
