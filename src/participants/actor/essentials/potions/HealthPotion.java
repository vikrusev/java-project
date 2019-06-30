package participants.actor.essentials.potions;

import messages.player.BackpackMessages;
import participants.actor.essentials.Treasure;
import participants.actor.player.Player;
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
