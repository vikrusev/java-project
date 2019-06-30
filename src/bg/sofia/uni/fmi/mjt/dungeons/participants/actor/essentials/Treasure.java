package bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials;

import bg.sofia.uni.fmi.mjt.dungeons.messages.player.BackpackMessages;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.Position;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.arsenal.Spell;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.arsenal.Weapon;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.potions.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.potions.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;

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

        // multiply by appropriate number, because spells have more damage than weapons - 3
        int power = (randomNumber % (spellBound - weaponBound)) * 3;

        if (isEpic()) {
            power *= 2;
            name.append(BRUTAL).append(" ");
        }

        int typeSpell = (int) (Math.random() * SPELL_NAMES.length);
        name.append(SPELL_NAMES[typeSpell]);

        return new Spell(name.toString(), power, power / 2);
    }

    // if the lottery's number is a cool number the weapon / spell is epic
    private static boolean isEpic() {
        int random = (int) (Math.random() * 101);
        return random % 7 == 0;
    }

    public BackpackMessages collect(Player player) {
        return null;
    }

    public String getName() {
        return this.name;
    }

}
