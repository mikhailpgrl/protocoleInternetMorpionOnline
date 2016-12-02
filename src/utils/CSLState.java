package utils;

/**
 * @authors POGORELOV Mikhail et CHIEV Alexandre
 * Enumeration d'états possibles d'un runnable client-server
 *
 */
public enum CSLState {
	nothing,
	waiting_refuse_error_create_addr, // Play ID
	waiting_error_create_server, // Answer Y
	waiting_ok,
	waiting_yourid,
	waiting_ok_exit
}
