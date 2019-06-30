package bg.sofia.uni.fmi.mjt.dungeons.server.services.engine;

import bg.sofia.uni.fmi.mjt.dungeons.messages.map.MapMessages;
import bg.sofia.uni.fmi.mjt.dungeons.messages.map.MapObjects;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.Actor;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.Position;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.Treasure;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.monster.Minion;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;
import bg.sofia.uni.fmi.mjt.dungeons.server.services.server.GameServerService;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static bg.sofia.uni.fmi.mjt.dungeons.messages.map.MapMessages.*;

public class GameMap {

    private Path mapFilePath;

    private ArrayList<ArrayList<Character>> map;
    private ArrayList<ArrayList<Character>> defaultMap = new ArrayList<>();

    private ArrayList<Treasure> treasures = new ArrayList<>();
    private ArrayList<Minion> minions = new ArrayList<>();

    public GameMap(String mapFileName) {

        this.mapFilePath = Path.of(mapFileName);

        readDefaultMap();

        this.map = new ArrayList<>(this.defaultMap);
    }

    private void readDefaultMap() {

        try (Stream<String> lines = Files.lines(mapFilePath)) {

            AtomicInteger lineCounter = new AtomicInteger(0);
            lines.dropWhile(line -> !line.equals(MAP_BEGIN.toString()))
                    .skip(1)
                    .filter(line -> !(line.isBlank() || line.isEmpty()))
                    .forEach(line -> {
                        try {
                            populateDefaultMap(line, lineCounter);
                            lineCounter.addAndGet(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void populateDefaultMap(String line, AtomicInteger lineCounterTake) throws Exception {

        int lineCounter = lineCounterTake.get();

        // we want to have the map in the most raw version - no blank spaces
        line = line.replaceAll(" ", "");

        ArrayList<Character> rowSymbols = new ArrayList<>();

        for (int i = 0; i < line.length(); ++i) {

            char symbol = line.charAt(i);

            if (isSymbolValid(symbol)) {
                rowSymbols.add(symbol);

                if (symbol == 'T') {
                    addTreasure(lineCounter, i);
                }

                if (symbol == 'M') {
                    addMinion(lineCounter, i);
                }
            }
            else {
                throw new Exception(UNKNOWN_SYMBOL.toString());
            }
        }

        this.defaultMap.add(rowSymbols);
    }

    private boolean isSymbolValid(char symbol) {
        return symbol == ' ' || MapObjects.getAllSymbols().contains(symbol);
    }

    public ArrayList<ArrayList<Character>> getCurrentMap() {
        return this.map;
    }

    public String getCurrentMapAsString() {

        StringBuilder currentMapAsString = new StringBuilder();

        for (ArrayList<Character> row : this.map) {

            Iterator<Character> iterator = row.iterator();
            while (iterator.hasNext()) {
                char symbol = iterator.next();
                currentMapAsString.append(symbol);

                if (iterator.hasNext()) {
                    currentMapAsString.append(" ");
                }
            }

            currentMapAsString.append(System.lineSeparator());
        }

        return currentMapAsString.toString();
    }

    public boolean addPlayer(Player player) {
        Position randomPosition = getRandomXY();

        if (randomPosition == null) {
            return false;
        }

        int x = randomPosition.getX();
        int y = randomPosition.getY();
        char playerIndex = player.getIndex();

        map.get(x).set(y, playerIndex);
        player.setPosition(x, y);

        return true;
    }

    private void addTreasure(int x, int y) {
        this.treasures.add(new Treasure(x, y));
    }

    public void addNewRandomTreasure() {
        Position randomPosition = getRandomXY();

        if (randomPosition == null) {
            return;
        }

        addNewFixedTreasure(randomPosition.getX(), randomPosition.getY());
    }

    public void addNewFixedTreasure(int x, int y) {
        addTreasure(x, y);
        this.map.get(x).set(y, 'T');
    }

    private void addMinion(int x, int y) {
        int level = (int) (Math.random() * 5 + 1);

        this.minions.add(new Minion(level, x, y));
    }

    public void addNewRandomMinion() {
        Position randomPosition = getRandomXY();

        if (randomPosition == null) {
            return;
        }

        int x = randomPosition.getX();
        int y = randomPosition.getY();
        addMinion(x, y);
        this.map.get(x).set(y, 'M');
    }

    public void removeTreasure(int x, int y) {
        // we will always have that treasure
        this.treasures.remove(findTreasure(x, y));
    }

    private Treasure findTreasure(int x, int y) {
        for (Treasure treasure : this.treasures) {
            Position treasurePosition = treasure.getPosition();

            if (treasurePosition.getX() == x && treasurePosition.getY() == y) {
                return treasure;
            }
        }

        return null;
    }

    public Minion findMinion(int x, int y) {
        for (Minion minion : this.minions) {
            Position minionPosition = minion.getPosition();

            if (minionPosition.getX() == x && minionPosition.getY() == y) {
                return minion;
            }
        }

        return null;
    }

    public Player findPlayer(int x, int y) {
        ConcurrentHashMap<SocketChannel, Player> users = GameServerService.getUsers();

        for (Player player : users.values()) {
            Position playerPosition = player.getPosition();

            if (playerPosition.getX() == x && playerPosition.getY() == y) {
                return player;
            }
        }



        return null;
    }

    private Position getRandomXY() {
        // at which free space to be added
        // collisions may happen as it is this method is not thread-safe, which is ok
        int random = (int) (Math.random() * 50 + 1);
        int previousRandom;

        int x = 0;
        int y = 0;

        do {
            previousRandom = random;
            for (ArrayList<Character> col : map) {
                for (Character place : col) {

                    if (place == '.') {
                        --random;
                    }

                    if (random == 0) {
                        return new Position(x, y);
                    }
                    ++y;
                }
                ++x;
                y = 0;
            }
        }
        // if (random == previousRandom) then there is no free space
        while (random > 0 || random != previousRandom);

        return null;
    }

    private void removeActorFromMap(Actor actor) {
        int actorX = actor.getPosition().getX();
        int actorY = actor.getPosition().getY();

        this.map.get(actorX).set(actorY, '.');
    }

    public void removePlayer(Player player) {
        removeActorFromMap(player);
    }

    public void removeMinion(Minion minion) {
        removeActorFromMap(minion);

        this.minions.remove(minion);
    }

    public MapMessages checkPosition(int x, int y) {
        return moveStatus(x, y);
    }

    private MapMessages moveStatus(int x, int y) {
        try {
            char symbol = this.map.get(x).get(y);

            if (symbol == '#') {
                return OBSTACLE;
            }

            if (symbol == 'M') {
                return MONSTER;
            }

            if (symbol == 'T') {
                return TREASURE;
            }

            if (symbol >= '0' && symbol <= '9') {
                return PLAYER;
            }

            return MOVE_OK;
        } catch (IndexOutOfBoundsException e) {
            return OUT_OF_BOUNDS;
        }
    }

    public MapMessages movePlayer(Player player, int x, int y) {
        char playerIndex = player.getIndex();

        Position currentPlayerPosition = player.getPosition();
        int currentX = currentPlayerPosition.getX();
        int currentY = currentPlayerPosition.getY();

        player.setPosition(x, y);

        if (findTreasure(currentX, currentY) != null) {
            // there is an unpicked treasure
            this.map.get(currentX).set(currentY, 'T');
        }
        else {
            // there was no treasure
            this.map.get(currentX).set(currentY, '.');
        }

        this.map.get(x).set(y, playerIndex);

        RESPONSE.setMessage("");
        return RESPONSE;
    }

}
