package game;

public class Platforme {

	/**
	 * Player1 (client) fait le premier coup joue avec les 1 => X 
	 * Player2 (server) joue avec les 2 => 0
	 * -1 case vide
	 */
	
	private int[][] platforme;
	private int currentWinner; // 1er ou 2nd joueur 

	public Platforme() {
		currentWinner = 0;
		this.platforme = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.platforme[i][j] = -1;
			}
		}
		System.out.println("Platforme est initialise");
	}
	
		
	public int[][] getPlatforme() {
		return this.platforme;
	}

	




	public int getCurrentWinner() {
		return currentWinner;
	}




	public void setCurrentWinner(int currentWinner) {
		this.currentWinner = currentWinner;
	}
	
	




	/**
	 * 
	 * @param pos
	 * @param isPlayer1
	 */
	public boolean put(int pos,int player_num){
		
		System.out.println("player n°" + player_num);
		
		switch (pos) {
		case 0:
			if(this.platforme[0][0] == -1){
				this.platforme[0][0] = player_num;
				return true;
			}else{
				return false;
			}
		case 1:
			if(this.platforme[0][1] == -1){
				this.platforme[0][1] = player_num;
				return true;
			}else{
				return false;
			}
		case 2:
			if(this.platforme[0][2] == -1){
				this.platforme[0][2] = player_num;
				return true;
			}else{
				return false;
			}
		case 3:
			if(this.platforme[1][0] == -1){
				this.platforme[1][0] = player_num;
				return true;
			}else{
				return false;
			}
		case 4:
			if(this.platforme[1][1] == -1){
				this.platforme[1][1] = player_num;
				return true;
			}else{
				return false;
			}
		case 5:
			if(this.platforme[1][2] == -1){
				this.platforme[1][2] = player_num;
				return true;
			}else{
				return false;
			}
		case 6:
			if(this.platforme[2][0] == -1){
				this.platforme[2][0] = player_num;
				return true;
			}else{
				return false;
			}
		case 7:
			if(this.platforme[2][1] == -1){
				this.platforme[2][1] = player_num;
				return true;
			}else{
				return false;
			}
		case 8:
			if(this.platforme[2][2] == -1){
				this.platforme[2][2] = player_num;
				return true;
			}else{
				return false;
			}
		default:
			return false;
		}
	}
	
	public void refresh(){
		currentWinner = 0;
		this.platforme = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.platforme[i][j] = -1;
			}
		}
		System.out.println("Platforme est initialise");
	}

	/**
	 * Renvoi true s'il n'y a plus de cases libres
	 * @return
	 */
	public boolean isFull(){
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if(this.platforme[i][j] != -1)
					return false;
			}
		}
		return true;
	}
	
	public void show(){
		char s;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				switch (platforme[i][j]) {
				case 1:
					s = '0';
					break;
				case 2:
					s = 'X';
					break;
				default:
					s = '_';
					break;
				}
				System.out.print("| " + s + " |");
			}
			System.out.println();
		}
	}
	
	
	
	
	public void show2(){
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print("| " + platforme[i][j] + " |");
			}
			System.out.println();
		}
	}
	
}
