package game;

import client.Client;

public class GameHandler {
		
	/**
	 * Player1 (client) 1
	 * Player2 (serveur) 2
	 * 
	 * 
	 * Remarque:
	 * Si le player2 a 3 '0' alignés le player1 doit effectuer le dernier coup
	 * 
	 */


	
	
	/**
	 * Verifie si l'un des deux joueurs gagne
	 * @param p : platforme de jeux
	 * @param player : joueur courrant
	 * @return : reponse (YouWin ou Draw)
	 */
	public static String checkPlatform(Platforme p){
		int win = 0;
		if((p.getPlatforme()[0][0] == 1) && (p.getPlatforme()[0][1] == 1) && (p.getPlatforme()[0][2] == 1)){
			win = 1;
		}
		if((p.getPlatforme()[1][0] == 1) && (p.getPlatforme()[1][1] == 1) && (p.getPlatforme()[1][2] == 1)){
			win = 1;
		}
		if((p.getPlatforme()[2][0] == 1) && (p.getPlatforme()[2][1] == 1) && (p.getPlatforme()[2][2] == 1)){
			win = 1;
		}
		if((p.getPlatforme()[0][0] == 1) && (p.getPlatforme()[1][1] == 1) && (p.getPlatforme()[2][2] == 1)){
			win = 1;
		}
		if((p.getPlatforme()[0][2] == 1) && (p.getPlatforme()[1][1] == 1) && (p.getPlatforme()[2][0] == 1)){
			win = 1;
		}
		if((p.getPlatforme()[0][0] == 1) && (p.getPlatforme()[1][0] == 1) && (p.getPlatforme()[2][0] == 1)){
			win = 1;
		}
		if((p.getPlatforme()[0][1] == 1) && (p.getPlatforme()[1][1] == 1) && (p.getPlatforme()[2][1] == 1)){
			win = 1;
		}
		if((p.getPlatforme()[0][2] == 1) && (p.getPlatforme()[1][2] == 1) && (p.getPlatforme()[2][2] == 1)){
			win = 1;
		}
		
		
		
		
		
		if((p.getPlatforme()[0][0] == 2) && (p.getPlatforme()[0][1] == 2) && (p.getPlatforme()[0][2] == 2)){
			win = 2;
		}
		if((p.getPlatforme()[1][0] == 2) && (p.getPlatforme()[1][1] == 2) && (p.getPlatforme()[1][2] == 2)){
			win = 2;
		}
		if((p.getPlatforme()[2][0] == 2) && (p.getPlatforme()[2][1] == 2) && (p.getPlatforme()[2][2] == 2)){
			win = 2;
		}
		if((p.getPlatforme()[0][0] == 2) && (p.getPlatforme()[1][1] == 2) && (p.getPlatforme()[2][2] == 2)){
			win = 2;
		}
		if((p.getPlatforme()[0][2] == 2) && (p.getPlatforme()[1][1] == 2) && (p.getPlatforme()[2][0] == 2)){
			win = 2;
		}
		if((p.getPlatforme()[0][0] == 2) && (p.getPlatforme()[1][0] == 2) && (p.getPlatforme()[2][0] == 2)){
			win = 2;
		}
		if((p.getPlatforme()[0][1] == 2) && (p.getPlatforme()[1][1] == 2) && (p.getPlatforme()[2][1] == 2)){
			win = 2;
		}
		if((p.getPlatforme()[0][2] == 2) && (p.getPlatforme()[1][2] == 2) && (p.getPlatforme()[2][2] == 2)){
			win = 2;
		}
		
		if(win == 1){
			return Client.youWin;
		}
		if(p.isFull()){
			return Client.draw;
		}
		
		
		
		
		
		
		
		
		return null;
		
	}
	
	
	
	
	
}
