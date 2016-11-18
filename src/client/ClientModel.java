package client;



import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class ClientModel {

	private String id;
	private Socket socket;
	private boolean isInGame;
	private String address;


	/**
	 *  Stock les informations du client dans le modele
	 * @param id
	 * @param s
	 */

	public ClientModel(String id, Socket s) {
		this.id = id;
		this.socket = s;
		this.isInGame = false;
		this.address = socket.getInetAddress().toString();
	}




	public Socket getSocket() {
		return socket;
	}




	public void setSocket(Socket socket) {
		this.socket = socket;
	}




	public String getAddress() {
		return address;
	}




	public void setAddress(String address) {
		this.address = address;
	}




	public boolean isInGame() {
		return isInGame;
	}
	public void setInGame(boolean isInGame) {
		this.isInGame = isInGame;
	}





	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Socket getMySocket() {
		return socket;
	}
	public void setMySocket(Socket mySocket) {
		this.socket = mySocket;
	}


	public OutputStream getOutputStream(){
		try (OutputStream os = this.socket.getOutputStream();) {
			return os;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



}
