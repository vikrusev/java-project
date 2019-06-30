package participants.actor.player;

import messages.player.BackpackMessages;
import participants.actor.Actor;
import participants.actor.Position;
import participants.actor.Stats;
import participants.actor.essentials.Treasure;
import participants.actor.essentials.arsenal.Spell;
import participants.actor.essentials.arsenal.Weapon;
import participants.actor.monster.Minion;

public final class Player implements Actor {

    private static boolean[] playerIndices = new boolean[] {
                                                false, false, false,
                                                false, false, false,
                                                false, false, false
                                            };

    public static boolean[] getPlayerIndices() {
        return playerIndices;
    }


    private String username;
    private char index;

    private int experience;
    private int experienceToLevelUp = 100;

    private Position position;
    private Backpack backpack = new Backpack();

    private Weapon weapon = null;
    private Spell spell = null;

    private Stats stats;

    private boolean isFighting = false;
    private Minion minionAgainst = null;
    private Player playerAgainst = null;

    public Player(String username) {
        this(username, -1, -1);
    }

    private Player(String username, int x, int y) {
        this.username = username;
        this.index = findFreeIndex();

        this.stats = new Stats(1, 1);
        this.experience = 0;

        this.setPosition(x, y);
    }

    private char findFreeIndex() {
        char index = '#';

        // there will always be a free index on player creation
        for (int i = 0; i < playerIndices.length; ++i) {
            if (!playerIndices[i]) {
                playerIndices[i] = true;
                index = (char) ((i + 1) + '0');
                break;
            }
        }

        return index;
    }

    public char getIndex() {
        return this.index;
    }

    public String getName() {
        return this.username;
    }

    public int getAttack() {
        return this.stats.getAttack();
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    public Spell getSpell() {
        return this.spell;
    }


    public Backpack getBackpack() {
        return this.backpack;
    }

    public void takeHealing(int healingPoints) {
        if (isAlive()) {
            this.stats.heal(healingPoints);
        }
    }

    public void takeMana(int manaPoints) {
        if (isAlive()) {
            this.stats.replenish(manaPoints);
        }
    }

    public void takeExperience(int experiencePoints) {
        this.experience += experiencePoints;

        if (this.experience >= experienceToLevelUp) {
            levelUp();
        }
    }

    public void equipWeapon(Weapon weapon) {
        if (weapon == null) {
            return;
        }

        if (this.weapon == null) {
            this.weapon = new Weapon(weapon.getName(), weapon.getDamage());
            return;
        }

        this.backpack.add(this.weapon);
        this.weapon = weapon;
    }

    public void equipSpell(Spell spell) {
        if (spell == null) {
            return;
        }

        if (this.spell == null) {
            this.spell = new Spell(spell.getName(), spell.getDamage(), spell.getManaCost());
            return;
        }

        this.backpack.add(this.spell);
        this.spell = spell;
    }

    private void levelUp() {
        this.stats.raise();
        this.refillStats();

        this.experience = this.experience % experienceToLevelUp;
        this.experienceToLevelUp += 50;
    }

    public BackpackMessages pickEssential(Treasure essential) {
        return this.backpack.add(essential);
    }

    /* ----- FIGHTING SCENES ----- */
    public void setFightMob(Minion minion) {
        this.minionAgainst = minion;
    }

    public void setFightPlayer(Player player) {
        this.playerAgainst = player;
    }

    public boolean isFightingMob() {
        return this.minionAgainst != null;
    }

    public boolean isFightingPlayer() {
        return this.playerAgainst != null;
    }

    public synchronized void setFightingMode(boolean isFighting) {
        this.isFighting = isFighting;
    }

    public boolean isFighting() {
        return this.isFighting;
    }

    public Minion getMinionAgainst() {
        return this.minionAgainst;
    }

    public Player getPlayerAgainst() {
        return this.playerAgainst;
    }

    public void endFight() {
        this.minionAgainst = null;
        this.playerAgainst = null;
        this.isFighting = false;
    }

    public Treasure dropRandomItem() {
        int backpackSize = this.backpack.getSize();

        if (backpackSize > 0) {
            int randomIndex = (int) (Math.random() * (backpackSize - 1));
            Treasure droppedItem = this.backpack.getSingleItem(randomIndex);
            this.backpack.remove(randomIndex);
            return droppedItem;
        }

        return null;
    }

    /* ----- OVERRIDING FROM ACTOR INTERFACE ----- */
    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public int getLevel() {
        return this.stats.getLevel();
    }

    @Override
    public int getHealth() {
        return this.stats.getHealth();
    }

    @Override
    public int getMana() {
        return this.stats.getMana();
    }

    @Override
    public int getExperience() {
        return this.experience;
    }

    @Override
    public int getDefense() {
        return this.stats.getDefense();
    }

    @Override
    public boolean isAlive() {
        return this.stats.isAlive();
    }

    @Override
    public void setPosition(int x, int y) {
        this.position = new Position(x, y);
    }

    @Override
    public void refillStats() {
        this.stats.refill();
    }

    @Override
    public void takeDamage(int damagePoints) {
        this.stats.reduceHealth(damagePoints);

        if (this.stats.getHealth() <= 0) {
            die();
        }
    }

    @Override
    public void die() {
        this.stats.death();
    }

    @Override
    public int normalAttack() {
        int damage = this.stats.getAttack();

        if (this.weapon == null) {
            return damage;
        }

        return damage + this.weapon.getDamage();
    }

    public int spellAttack() {

        int damage = -1;

        if (this.spell == null) {
            return damage;
        }

        if (this.stats.getMana() >= this.spell.getManaCost()) {
            this.stats.reduceMana(this.spell.getManaCost());

            return damage + this.spell.getDamage();
        }

        return damage;
    }

    /* ----- OVERRIDING HASH FUNCTIONS ----- */
    @Override
    public int hashCode() {
        return this.username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Player))
            return false;

        return ((Player) obj).getName().equals(this.username);
    }

}
