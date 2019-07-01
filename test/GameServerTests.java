import messages.map.MapMessages;
import participants.actor.monster.Minion;
import participants.actor.player.Player;
import server.GameEngine;
import server.services.map.engine.GameMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameServerTests implements TestUtils {

    private GameEngine gameEngine;
    private GameMap gameMap;
    private String map;

    private Player player;
    private Minion minion;

    String mapFilePath = "resources/default-map.txt";

    @Before
    public void init() {
        gameEngine = new GameEngine(mapFilePath);
        gameMap = gameEngine.getMap();
        map = createTestMap();
        player = createTestPlayer();
        minion = createTestMinion();
    }

    @Test
    public void testPlayerStats() {
        assertEquals(map, gameMap.getCurrentMapAsString());
    }

    @Test
    public void playerMovesCorrectly() {
        MapMessages response = gameMap.movePlayer(player, 3, 3);

        assertEquals(response.toString(), "");
    }

    @Test
    public void findMinionCorrectly() {
        Minion foundMinion = gameMap.findMinion(0, 0);

        assertEquals(minion.getPosition().getX(), foundMinion.getPosition().getX());
        assertEquals(minion.getPosition().getY(), foundMinion.getPosition().getY());
        assertTrue(minion.isAlive());
    }
}
