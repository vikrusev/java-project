package participants.actor;

public interface Actor {

    /**
     * @return Position - the position of the Actor (a player or a minion)
     */
    Position getPosition();

    /**
     * Sets teh position of the Actor.
     * @param x - abcissa on the map.
     * @param y - ordinate on the map.
     */
    void setPosition(int x, int y);

    /**
     * @return int - the current level of the Actor (a player or a minion)
     */
    int getLevel();

    /**
     * @return int - the current health of the Actor (a player or a minion)
     */
    int getHealth();

    /**
     * @return int - the current mana of the Actor (a player or a minion)
     */
    int getMana();

    /**
     * @return int - the current experience of the Actor (a player or a minion)
     */
    int getExperience();

    /**
     * @return int - the current defense of the Actor (a player or a minion)
     */
    int getDefense();

    /**
     * @return int - the current normal attack of the Actor (a player or a minion)
     */
    int normalAttack();

    /**
     * Reduces the health points of the Actor (when it has been attacked)
     * @param damagePoints - the input damage
     */
    void takeDamage(int damagePoints);

    /**
     * Refills the stats to the maximum for the Actor.
     */
    void refillStats();

    /**
     * If the Actor is currently in a fight.
     * @return - if the actor is fighting
     */
    boolean isFighting();

    /**
     * Sets the state of the Actor to either fighting or not.
     * @param isFighting - if the Actor will be fighting or not.
     */
    void setFightingMode(boolean isFighting);

    /**
     * Simulates the death of an Actor.
     * It sets stats.alive to false and stats.health to 0.
     */
    void die();

    /**
     * If the Actor is still alive.
     * @return boolean - wheather or not the Actor is alive.
     */
    boolean isAlive();

}
