package participants.actor.essentials.arsenal;

import messages.player.BackpackMessages;
import participants.actor.essentials.Treasure;
import participants.actor.player.Player;

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
