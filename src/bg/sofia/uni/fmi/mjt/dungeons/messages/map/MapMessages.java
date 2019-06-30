package bg.sofia.uni.fmi.mjt.dungeons.messages.map;

public enum MapMessages {

    // creation of map
    MAP_BEGIN("MAP BEGIN:"), UNKNOWN_SYMBOL("Unknown symbol found"),

    // player movement
    MOVE_OK("okay to move"),
    TREASURE("Treasure found"), PLAYER("Do you want to fight the player with hp of %d? (y/n)"),
    PLAYER_ALREADY_FIGHTING("This player is already fighting"),
    OBSTACLE("There is an obstacle"), MONSTER("Do you want to fight the monster level %d? (y/n)"),
    OUT_OF_BOUNDS("You can not go out of the map"), RESPONSE_TEXT(""),
    KILLED_MINION("=> you killed the monster and gained %d experience"),
    KILLED_PLAYER("=> you killed the player and gained %d experience"),
    NORMAL_ATTACK_MINION("=> you hit the monster with a normal attack " +
                        "| damage dealt: %d | hp left: %d | damage taken: %d | your hp: %d |"),
    SPELL_ATTACK_MINION("=> you hit the monster with a spell attack " +
                        "| damage dealt: %d | hp left: %d | damage taken: %d | your hp: %d |"),
    NORMAL_ATTACK_PLAYER("=> you hit the player with a normal attack | damage dealt: %d | hp left: %d |"),
    SPELL_ATTACK_PLAYER("=> you hit the player with a spell attack | damage dealt: %d | hp left: %d |"),
    YOU_DIED("=> you died"), HIT_YOU("=> %s hit you with %d damage | hp_left: %d |"),
    FIGHT_NOBODY("=> you are not fighting with anybody"),

    RESPONSE("");

    private String message;

    MapMessages(String message) {
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
