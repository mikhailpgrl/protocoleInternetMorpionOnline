package utils;

/**
 * @authors POGORELOV Mikhail et CHIEV Alexandre
 * Enumeration d'états possibles du ServerHandlerThread
 *
 */
public enum SHState {
	nothing,
	waiting_port,
	waiting_ok,
	waiting_ok_before_start,
	waiting_answer // Reponse au AskGame
}
