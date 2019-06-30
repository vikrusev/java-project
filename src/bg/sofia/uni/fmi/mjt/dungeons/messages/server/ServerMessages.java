package bg.sofia.uni.fmi.mjt.dungeons.messages.server;

public enum ServerMessages {

    // server specific messages
    SERVER_STARTED("--- server started on %s%n"), CANNOT_START("Server could not be started"),
    CANNOT_CLOSE("Server could not be auto-closed"), CLIENT_CONNECTED("--- a client has connected"),
    FATAL_ERROR_CLOSING_SOCKET("--- could not close a socket"),
    HAS_CONNECTED("%s has connected%n"), HAS_DISCONNECTED("%s has disconnected%n"),
    SOCKETS_REMAINING("sockets still opened: %d%n"),
    PLAYER_NOT_FOUND("Player could not be found"),

    // server responses messages
    CONNECTED_OK("=> successfully connected"),
    DISCONNECT_OK("=> successfully disconnected"), DISCONNECT_FAIL("=> could not disconnect"),
    MISSING_USERNAME("=> missing username"), USERNAME_EXISTS("=> the username is taken"),
    NO_COMMAND("=> no command entered"), UNKNOWN_COMMAND("=> unknown command"),
    UNKNOWN_COMMAND_FIGHT("=> unknown command. you are in a fight"),
    OTHER_BACKPACK_FULL("=> other player's backpack is full"),
    PLAYER_NOT_NEAR("=> this player is not close to you"),
    SHARE_SUCCESS("=> you successfully shared an item"), HAS_GIVEN("=> player %c gave you an item - %s"),
    ITEM_NOT_POTION("=> the item is not a potion"), USED_POTION("=> you used the potion"),
    SPELL_EQUIPPED("=> spell equiped"), WEAPON_EQUIPPED("=> weapon equipped"),
    ITEM_NOT_SPELL("=> the item is not a spell"), ITEM_NOT_WEAPON("=> the item is not a weapon"),
    STOP_ALL_CLIENTS("themostrandomstringtoKILLTHEMALL12345678900987654321"), SERVER_FULL("=> the server is full"),
    YOU_ARE_DEAD("=> you are dead and cannot play anymore"), CANNOT_FLEE("=> you cannot run away"),

    // for any other response
    RESPONSE_TEXT("");


    private String message;

    ServerMessages(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}
