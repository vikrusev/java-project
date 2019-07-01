package participants.essentials.potions;

import participants.essentials.Treasure;
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

}
