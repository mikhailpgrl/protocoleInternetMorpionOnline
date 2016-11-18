package utils;

public enum CCLState {
	nothing,
	in_game, //Pos, Win, Lose, Draw, Exit
	waiting_ok,
	waiting_regame_client, // YouStart Pos Exit
	waiting_regame_server, // Yes Exit
	
}
