package bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.arsenal;

import bg.sofia.uni.fmi.mjt.dungeons.messages.player.BackpackMessages;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.Treasure;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;

public class Weapon extends Treasure {

    private int damage;

    public Weapon(String name, int damage) {
        super(name);

        this.damage = damage;
    }

    public int getDamage() {
        return this.damage;
    }

    @Override
    public BackpackMessages collect(Player player) {
        return player.pickEssential(this);
    }

}
