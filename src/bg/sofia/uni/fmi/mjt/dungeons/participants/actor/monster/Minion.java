package bg.sofia.uni.fmi.mjt.dungeons.participants.actor.monster;

import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.Position;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.Stats;

public final class Minion implements Actor {

    private Position position;
    private Stats stats;

    private int experience;

    private boolean isFighting = false;

    public Minion(int level, int x, int y) {

        // the monster has 50% power of a player on level 1
        double statsMultiplier = 0.5 * level;
        this.stats = new Stats(level, statsMultiplier);
        this.experience = 10 * level;

        this.setPosition(x, y);
    }

    public void setFightingMode(boolean isFighting) {
        this.isFighting = isFighting;
    }

    public boolean isFighting() {
        return this.isFighting;
    }

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
        return this.stats.getAttack();
    }

}
