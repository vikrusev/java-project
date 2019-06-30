package messages.player;

public enum PlayerMessages {

    // participants specific messages
    CONNECT_FIRST("=> player must connect to the server first"),
    OPENED_SOCKET("=> successfully opened a new socket"),
    DISCONNECT_OK("=> successfully disconnected"),
    ERROR_CONNECTING("=> could not connect to server on %s:%d, make sure that the server is started"),
    SERVER_CLOSED("=> the server has shutdown unexpectedly"),

    // server exception messages
    SOCKET_CLOSED("Socket closed"),
    CONNECTION_RESET("Connection reset");

    private String message;

    PlayerMessages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}