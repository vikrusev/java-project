package bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.arsenal;

import bg.sofia.uni.fmi.mjt.dungeons.messages.player.BackpackMessages;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.Treasure;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;
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

    @Override
    public BackpackMessages collect(Player player) {
        return player.pickEssential(this);
    }

}
