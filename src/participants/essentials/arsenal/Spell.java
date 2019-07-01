package participants.essentials.arsenal;

import participants.essentials.Treasure;
import com.google.gson.annotations.SerializedName;

public class Spell extends Treasure {

    private int damage;
    @SerializedName("mana_cost") private int manaCost;

    public Spell(String name, int damage, int manaCost) {
        super(name);

        this.damage = damage;
        this.manaCost = manaCost;
    }

    public int getManaCost() {
        return this.manaCost;
    }

    public int getDamage() {
        return this.damage;
    }

}
