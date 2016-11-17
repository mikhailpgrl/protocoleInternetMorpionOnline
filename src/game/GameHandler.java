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
		
		System.out.println("Current Winner = " + p.getCurrentWinner() + " sec =  " + p.getSecWinner());
		if(win == 1){
			if(p.getCurrentWinner() == 0){
				p.setCurrentWinner(win);
			}else{
				if(p.getCurrentWinner() == 1){
					p.setSecWinner(win);
					System.out.println("Cas 1");
					return Client.youWin;
				}
			}
		}
		if(win == 2){
			if(p.getCurrentWinner() == 0){
				System.out.println("Cas 2");
				return Client.youWin;
			}
			if(p.getCurrentWinner() == 1){
				return Client.draw;
			}
		}
		
		
		
		
		
		
		
		return null;
		
	}
	
	
	
	
	
}
