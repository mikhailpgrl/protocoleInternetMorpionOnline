package utils;

import java.io.IOException;
import java.net.ServerSocket;

import client.Client;
import client.Client.Current_state;

/**
 * @authors POGORELOV Mikhail et CHIEV Alexandre
 * Contient les utilitaires pour le client
 * 
 */
public class UtilsClient {
	/**
	 * Genere un tableau de ports puis 
	 * crée une socket sur le port libre
	 * @return
	 * @throws IOException
	 */
	public static ServerSocket create() throws IOException {
		int[] ports = new int[30];
		for (int i = 0; i < 30; i++) {
			ports[i] = i + 1000;
		}
	    for (int port : ports) {
	        try {
	            return new ServerSocket(port);
	        } catch (IOException ex) {
	            continue; // try next port
	        }
	    }
	    throw new IOException("no free port found");
	}
}
