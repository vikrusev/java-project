package participants.actor.player;

import messages.player.BackpackMessages;
import participants.actor.ActorExtended;
import participants.essentials.Treasure;
import participants.essentials.arsenal.Spell;
import participants.essentials.arsenal.Weapon;
import participants.actor.monster.Minion;

public final class Player extends ActorExtended {

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

    private int experienceToLevelUp = 100;

    private Backpack backpack = new Backpack();

    private Weapon weapon = null;
    private Spell spell = null;

    private Minion minionAgainst = null;
    private Player playerAgainst = null;

    /**
     * Creates a player with username and position (-1, -1)
     * @param username - the player's username
     */
    public Player(String username) {
        this(username, -1, -1);
    }

    /**
     * Creates a player with a username on specific place on the map.
     * Also sets the initial stats for a player.
     *
     * @param username - the player's username
     * @param x - abcissa on the map
     * @param y - ordinate on the map
     */
    private Player(String username, int x, int y) {
        super(1, 1, 0, x, y);

        this.username = username;
        this.index = findFreeIndex();
    }

    /**
     * Gets a free 'id' for the new player.
     * There will always be a free index as the GameServer will be handling maximum of 9 players.
     * @return char - we represent the player's position on the map as a number of type 'char'
     */
    private char findFreeIndex() {
        char index = '#';

        // find the first free index
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
        return super.stats.getAttack();
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

    /**
     * If the player is alive - heal him.
     * @param healingPoints - the amount of health to be healed.
     */
    public void takeHealing(int healingPoints) {
        if (isAlive()) {
            super.stats.heal(healingPoints);
        }
    }

    /**
     * If the player is alive - replenish him.
     * @param manaPoints - the amount of mana to be replenished.
     */
    public void takeMana(int manaPoints) {
        if (isAlive()) {
            super.stats.replenish(manaPoints);
        }
    }

    /**
     * Adds experience points to the player's experience.
     * If they exceed the limit of the current level - the player levels up.
     * @param experiencePoints - the experience points to be added.
     */
    public void takeExperience(int experiencePoints) {
        super.experience += experiencePoints;

        if (super.experience >= experienceToLevelUp) {
            levelUp();
        }
    }

    /**
     * If the player currently has no weapon - equip a copy of the passed weapon.
     *
     * If the player currently has weapon - leave the currently equipped weapon in the backpack
     * and equip the new one.
     *
     * @param weapon - the weapon to be equipped.
     */
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

    /**
     * If the player currently has no spell - equip a copy of the passed spell.
     *
     * If the player currently has spell - leave the currently equipped spell in the backpack
     * and equip the new one.
     *
     * @param spell - the spell to be equipped.
     */
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

    /**
     * Raises the default stats of the player (a.k.a. the maximum).
     * Refills all stats (health, mana).
     * Increments the needed experience points for the next level by 50.
     */
    private void levelUp() {
        super.stats.raise();
        super.refillStats();

        super.experience = super.experience % experienceToLevelUp;
        this.experienceToLevelUp += 50;
    }

    /**
     * Adds a new essential to the backpack.
     * @param essential - the essential to be added.
     * @return BackpackMessages - a message from the backpack.
     */
    public BackpackMessages pickEssential(Treasure essential) {
        return this.backpack.add(essential);
    }

    /**
     * Drops a random item upon death on the position where the player has died.
     * @return Treasure - the treasure that is dropped on the map.
     */
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

    /* ---------- FIGHTING SCENES ---------- */
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

    public Minion getMinionAgainst() {
        return this.minionAgainst;
    }

    public Player getPlayerAgainst() {
        return this.playerAgainst;
    }

    public void endFight() {
        this.minionAgainst = null;
        this.playerAgainst = null;

        super.setFightingMode(false);
    }

    public int spellAttack() {
        int damage =  super.stats.getAttack();

        if (this.spell == null) {
            return damage;
        }

        if (super.stats.getMana() >= this.spell.getManaCost()) {
            super.stats.reduceMana(this.spell.getManaCost());

            return damage + this.spell.getDamage();
        }

        return damage;
    }

    /* ----- OVERRIDING FROM ACTOR EXTENDED CLASS ----- */
    @Override
    public int normalAttack() {
        int damage = super.stats.getAttack();

        if (this.weapon == null) {
            return damage;
        }

        return damage + this.weapon.getDamage();
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
