package bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.potions;

import bg.sofia.uni.fmi.mjt.dungeons.messages.player.BackpackMessages;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.Treasure;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;
import com.google.gson.annotations.SerializedName;

public class HealthPotion extends Treasure {

    private static final String name = "Health Potion (S)";

    @SerializedName("healing_points") private int healingPoints = 30;

    public HealthPotion() {
        super(name);
    }

    public int healingPoints() {
        return this.healingPoints;
    }

    @Override
    public BackpackMessages collect(Player player) {
        return player.pickEssential(this);
    }

}
