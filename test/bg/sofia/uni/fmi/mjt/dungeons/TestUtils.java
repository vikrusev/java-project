package bg.sofia.uni.fmi.mjt.dungeons;

import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.monster.Minion;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;

public interface TestUtils {

    default Player createTestPlayer() {
        Player player = new Player("player1");
        player.setPosition(2, 2);
        return player;
    }

    default Minion createTestMinion() {
        return new Minion(3, 0, 0);
    }

    default String createTestMap() {
        return "M T # # . . M M T T\r\n" +
                ". # # . . # # # # #\r\n" +
                ". . . . M . # . M T\r\n" +
                "# . M . . . # . # #\r\n" +
                ". M . . . # # . # T\r\n" +
                ". . . T . . # . # T\r\n" +
                ". # M # . . . T # M\r\n" +
                ". # # # . . . . # .\r\n" +
                "T . . . . T . . # .\r\n" +
                ". . M # # # . . . .\r\n";
    }
}
