package participants.actor;

public class ActorExtended implements Actor {

    protected Stats stats;
    protected int experience;

    private Position position;

    private boolean isFighting = false;

    protected ActorExtended(int level, double statsMultiplier, int experience, int x , int y) {
        this.stats = new Stats(level, statsMultiplier);
        this.experience = experience;

        this.position = new Position(x, y);
    }

    protected void takeExperience(int experiencePoints) {
        this.experience += experiencePoints;
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(int x, int y) {
        this.position = new Position(x, y);
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
    public int normalAttack() {
        return this.stats.getAttack();
    }

    @Override
    public void takeDamage(int damagePoints) {
        this.stats.reduceHealth(damagePoints);

        if (this.stats.getHealth() <= 0) {
            die();
        }
    }

    @Override
    public void refillStats() {
        this.stats.refill();
    }

    @Override
    public boolean isFighting() {
        return this.isFighting;
    }

    @Override
    public synchronized void setFightingMode(boolean isFighting) {
        this.isFighting = isFighting;
    }

    @Override
    public void die() {
        this.stats.death();
    }

    @Override
    public boolean isAlive() {
        return this.stats.isAlive();
    }

}
