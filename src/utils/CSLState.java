package utils;

public enum CSLState {
	nothing,
	waiting_refuse_error_create_addr, // Play ID
	waiting_error_create_server, // Answer Y
	waiting_ok,
	waiting_yourid,
	waiting_ok_exit
}
