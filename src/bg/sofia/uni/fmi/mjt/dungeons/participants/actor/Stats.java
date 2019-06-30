package bg.sofia.uni.fmi.mjt.dungeons.participants.actor;

public class Stats {

    private double statMultiplier;
    private int level;
    private int health = 100;
    private int mana = 100;
    private int attack = 30;
    private int defense = 10;
    private boolean alive = true;

    private int maxHealth;
    private int maxMana;

    public Stats(int level, double statMultiplier) {
        this.level = level;
        this.statMultiplier = statMultiplier;

        this.health *= statMultiplier;
        this.mana *= statMultiplier;
        this.attack *= statMultiplier;
        this.defense *= statMultiplier;

        this.maxHealth = this.health;
        this.maxMana = this.mana;
    }

    public void heal(int healingPoints) {
        this.health = Math.min(this.maxHealth, this.health + healingPoints);
    }

    public void replenish(int manaPoints) {
        this.mana = Math.min(this.maxMana, this.mana + manaPoints);
    }

    public void raise() {
        this.level += 1 * this.statMultiplier;
        this.maxHealth += 10 * this.statMultiplier;
        this.maxMana += 10 * this.statMultiplier;
        this.attack += 5 * this.statMultiplier;
        this.defense += 5 * this.statMultiplier;
    }

    public void refill() {
        this.health = this.maxHealth;
        this.mana = this.maxMana;
    }

    public void reduceMana(int reduceWith) {
        this.mana -= reduceWith;
    }

    public void reduceHealth(int reduceWith) {
        this.health -= reduceWith;
    }

    public void death() {
        this.health = 0;
        this.alive = false;
    }

    public int getLevel() {
        return this.level;
    }

    public int getHealth() {
        return this.health;
    }

    public int getMana() {
        return this.mana;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getDefense() {
        return this.defense;
    }

    public boolean isAlive() {
        return this.alive;
    }

}
