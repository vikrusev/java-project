package participants.essentials;

import messages.player.BackpackMessages;
import participants.actor.Position;
import participants.essentials.arsenal.Spell;
import participants.essentials.arsenal.Weapon;
import participants.essentials.potions.HealthPotion;
import participants.essentials.potions.ManaPotion;
import participants.actor.player.Player;

public class Treasure {

    private static final String[] WEAPON_NAMES = { "Sword", "Full Moon Sword", "Poison Sword",
                                                    "Triton Sword", "Rune Sword", "Zodiac Sword" };

    private static final String[] SPELL_NAMES = { "Dark Orb", "Spirit Strike", "Dragon Swirl",
                                                    "Sword Spin", "Three-Way Cut", "Life Force" };

    private static final String EPIC = "Epic";
    private static final String BRUTAL = "Brutal";

    private Position position;
    private String name;

    public Treasure(String name) {
        this.name = name;
    }

    public Treasure(int x, int y) {
        this.position = new Position(x, y);
    }

    public Position getPosition() {
        return this.position;
    }

    /**
     * Creates a random number from 0 to 100.
     * The higher the random number for weapon/spell, the more damage it has.
     *
     * The name of the weapon/spell is randomly picked from the array of weapons/spells.
     * Weapons/spells may have 'Epic'/'Brutal' prepended to their names if they are epic.
     *
     * @return Treasure - A weapon/spell healthPotion/manaPotion, based on constants.
     */
    public synchronized static Treasure randomTreasure() {

        int randomNumber = (int) (Math.random() * 101);

        // we will get a random treasure based on an interval of integers
        // normal potions have the same chance
        final int healthPotionBound = 20;
        final int manaPotionBound = healthPotionBound * 2;
        final int weaponBound = 80;
        final int spellBound = 100;

        // the damage of the weapon / spell is based on the random value
        // the closer to the upper bound, the stronger the weapon / spell
        StringBuilder name = new StringBuilder();

        if (randomNumber < healthPotionBound) {
            return new HealthPotion();

        } else if (randomNumber < manaPotionBound) {
            return new ManaPotion();

        } else if (randomNumber < weaponBound) {

            int power = randomNumber % (weaponBound - manaPotionBound) + 1;

            if (isEpic()) {
                power *= 2;
                name.append(EPIC).append(" ");
            }

            int typeWeapon = (int) (Math.random() * WEAPON_NAMES.length);
            name.append(WEAPON_NAMES[typeWeapon]);

            return new Weapon(name.toString(), power);
        }

        // multiply by appropriate number, because spells have more damage than weapons - 3 times more
        int power = (randomNumber % (spellBound - weaponBound)) * 3;

        if (isEpic()) {
            power *= 2;
            name.append(BRUTAL).append(" ");
        }

        int typeSpell = (int) (Math.random() * SPELL_NAMES.length);
        name.append(SPELL_NAMES[typeSpell]);

        return new Spell(name.toString(), power, power / 2);
    }

    /**
     * Creates a new random number.
     * One in seven weapons/spells is 'epic'
     *
     * @return boolean - If the number is a lucky number (one in 7).
     */
    private static boolean isEpic() {
        int random = (int) (Math.random() * 101);
        return random % 7 == 0;
    }

    public String getName() {
        return this.name;
    }

    public BackpackMessages collect(Player player) {
        return player.pickEssential(this);
    }

}
