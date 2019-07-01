package participants.essentials.potions;

import participants.essentials.Treasure;
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

}
