package bg.sofia.uni.fmi.mjt.dungeons.messages.server;

public enum GameEngineMessages {

    // connected with the map
    NO_SPACE("There is no free space on the map"),

    // GameEngine specific messages
    ;

    private String message;

    GameEngineMessages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}
