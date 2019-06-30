package participants.actor;

public interface Actor {

    Position getPosition();

    int getLevel();

    int getHealth();

    int getMana();

    int getExperience();

    int getDefense();

    int normalAttack();

    void takeDamage(int damagePoints);

    void setPosition(int x, int y);

    void refillStats();

    void die();

    boolean isAlive();

}
