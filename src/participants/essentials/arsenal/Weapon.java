package participants.essentials.arsenal;

import participants.essentials.Treasure;

public class Weapon extends Treasure {

    private int damage;

    public Weapon(String name, int damage) {
        super(name);

        this.damage = damage;
    }

    public int getDamage() {
        return this.damage;
    }

}
