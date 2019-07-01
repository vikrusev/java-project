package participants.actor.monster;

import participants.actor.ActorExtended;

public final class Minion extends ActorExtended {

    /**
     * Creates a new minion with the specified level and position.
     * The minion has 50% power of a player on level 1 (statsMultiplier == 0.5 * level)
     * Also sets the initial stats for a monster.
     *
     * @param level - the level of the monster.
     * @param x - abcissa on the map.
     * @param y - ordinate on the map.
     */
    public Minion(int level, int x, int y) {
        super(level, 0.5 * level, 10 * level, x, y);
    }

}
