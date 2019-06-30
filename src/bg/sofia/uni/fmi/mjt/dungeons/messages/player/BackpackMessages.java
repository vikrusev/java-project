package bg.sofia.uni.fmi.mjt.dungeons.messages.player;

public enum BackpackMessages {

    // picking items
    BACKPACK_FULL("The backpack is full"), BACKPACK_EMPTY("The backpack is empty"),
    ITEM_PICKED("The item has been added"),

    // modify backpack
    ITEM_REMOVED("The item has been removed"), ITEM_NOT_FOUND("The item has not been found");

    private String message;

    BackpackMessages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}
