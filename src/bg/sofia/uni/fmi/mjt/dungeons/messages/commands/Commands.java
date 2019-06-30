package bg.sofia.uni.fmi.mjt.dungeons.messages.commands;

public enum Commands {

    // Player -> GameServer commands
    CONNECT("connect"), DISCONNECT("disconnect"),

    // GameServer -> Player commands,
    UPDATE_SINGLE("update"), UPDATE_ALL("update-all"),
    SEND_MESSAGE("send"),

    // Player -> GameEngine commands
    UP("up"), DOWN("down"), LEFT("left"), RIGHT("right"),
    COLLECT_TREASURE("collect"), FIGHT("fight"), LIST_BACKPACK("list-backpack"),
    EQUIP_WEAPON("equip-weapon"), EQUIP_SPELL("use-spell"), USE_POTION("use-potion"),
    NORMAL_ATTACK("normal-attack"), SPELL_ATTACK("spell-attack"),
    SHARE_ITEM("share-item"), SHOW_BACKPACK("show-backpack"),

    // fighting answers
    CONFIRM_FIGHT_SMALL("y"), CONFIRM_FIGHT_BIG("Y"),
    DECLINE_FIGHT_SMALL("n"), DECLINE_FIGHT_BIG("N"),

    UNKNOWN_COMMAND("");

    private String message;

    Commands(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

    // find a command by the String-value
    public static Commands findCommand(String searchFor) {

        for (Commands command : Commands.values()) {
            if (searchFor.equals(command.toString())) {
                return command;
            }
        }

        return Commands.UNKNOWN_COMMAND;
    }

}
