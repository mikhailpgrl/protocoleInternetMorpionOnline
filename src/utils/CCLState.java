package utils;

/**
 * @authors POGORELOV Mikhail et CHIEV Alexandre
 * Enumeration d'états possibles d'un runnable client-client 
 *
 */
public enum CCLState {
	nothing,
	in_game, //Pos, Win, Lose, Draw, Exit
	waiting_ok,
	waiting_ok_exit,
	waiting_regame_client, // YouStart Pos Exit
	waiting_regame_server, // Yes Exit	
}
