package messages.server;

public enum GameEngineMessages {

    // connected with the map
    NO_SPACE("There is no free space on the map");

    private String message;

    GameEngineMessages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}
