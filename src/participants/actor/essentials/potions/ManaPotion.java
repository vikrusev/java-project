package participants.actor.essentials.potions;

import messages.player.BackpackMessages;
import participants.actor.essentials.Treasure;
import participants.actor.player.Player;
import com.google.gson.annotations.SerializedName;

public class ManaPotion extends Treasure {

    private static final String name = "Mana Potion (S)";

    @SerializedName("mana_points") private int manaPoints = 20;

    public ManaPotion() {
        super(name);
    }

    public int replenishPoints() {
        return this.manaPoints;
    }

    @Override
    public BackpackMessages collect(Player player) {
        return player.pickEssential(this);
    }

}
