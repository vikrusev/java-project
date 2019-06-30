package bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.potions;

import bg.sofia.uni.fmi.mjt.dungeons.messages.player.BackpackMessages;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.Treasure;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;
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
