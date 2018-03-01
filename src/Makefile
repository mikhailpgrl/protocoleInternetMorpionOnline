JAVAC = javac
JAVACFLAGS = -g
SRC= client/Client.java \
	client/ClientClientListener.java \
	client/ClientModel.java \
	client/ClientServerListener.java \
	server/Server.java \
	server/ServerHandlerThread.java \
	game/Platforme.java \
	game/GameHandler.java \
	utils/CCLState.java \
	utils/CSLState.java \
	utils/ClientStateHandler.java \
	utils/SHState.java \
	utils/UtilsClient.java \
	utils/UtilsServer.java




CLS= $(SRC:.java=.class)

all:  $(CLS)


.SUFFIXES : .class .java
.java.class :
	$(JAVAC) $(JAVACFLAGS) $<


jar: $(classes)
	jar cvf client.jar $(classes) 
