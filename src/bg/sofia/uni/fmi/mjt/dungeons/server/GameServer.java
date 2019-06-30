package bg.sofia.uni.fmi.mjt.dungeons.server;

import bg.sofia.uni.fmi.mjt.dungeons.messages.map.MapMessages;
import bg.sofia.uni.fmi.mjt.dungeons.messages.server.GameEngineMessages;
import bg.sofia.uni.fmi.mjt.dungeons.messages.server.ServerMessages;
import bg.sofia.uni.fmi.mjt.dungeons.messages.commands.Commands;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.monster.Minion;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;
import bg.sofia.uni.fmi.mjt.dungeons.server.services.server.ActionHandler;
import bg.sofia.uni.fmi.mjt.dungeons.server.services.server.GameServerService;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

import static bg.sofia.uni.fmi.mjt.dungeons.messages.map.MapMessages.YOU_DIED;
import static bg.sofia.uni.fmi.mjt.dungeons.messages.server.ServerMessages.*;
import static bg.sofia.uni.fmi.mjt.dungeons.messages.commands.Commands.*;

public final class GameServer extends GameServerService {

    private GameEngine engine;
    private ActionHandler actionHandler;

    private final int maxUsers = 9;

    public GameServer(int port, String mapFileName) throws IOException {
        super(port);

        this.engine = new GameEngine(mapFileName);
        this.actionHandler = new ActionHandler();

        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        System.out.printf(getEnumMessage(SERVER_STARTED), hostAddress);
    }

    private void start() throws IOException {
        while (runServer) {
            int readyChannels = selector.select();

            if (readyChannels ==  0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    accept(key);
                }
                else if (key.isReadable()) {
                    read(key);
                }

                keyIterator.remove();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);
        sc.register(this.selector, SelectionKey.OP_READ);

        System.out.println(CLIENT_CONNECTED);
    }

    private void read(SelectionKey key) {

        SocketChannel sc = (SocketChannel) key.channel();

        commandBuffer.clear();
        try {
            sc.read(commandBuffer);
            commandBuffer.flip();

            String command = Charset.forName("UTF-8").decode(commandBuffer).toString();
            ServerMessages response = executeCommand(command, sc);

            // no response needed for these cases
            if (response == RESPONSE_TEXT && "".equals(getEnumMessage(response))) {
                return;
            }

            if (response == DISCONNECT_OK) {
                return;
            }

            // internal server error - stop the server
            if (response == DISCONNECT_FAIL) {
                System.err.println(FATAL_ERROR_CLOSING_SOCKET);
                this.stop();
            }


            String responseText = getEnumMessage(response);

            commandBuffer.clear();
            commandBuffer.put(responseText.getBytes());
            commandBuffer.put(System.lineSeparator().getBytes());
            commandBuffer.put(System.lineSeparator().getBytes());

            commandBuffer.flip();
            while (commandBuffer.hasRemaining()) {
                sc.write(commandBuffer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServerMessages executeCommand(String receivedMessage, SocketChannel sc) {
        receivedMessage = receivedMessage.trim().replaceAll(" +", " ");

        String[] commandParts = receivedMessage.split(" ");

        if (commandParts.length == 0) {
            return NO_COMMAND;
        }

        String command = commandParts[0];
        Commands enumCommand = Commands.findCommand(command);

        if (enumCommand == CONNECT) {
            return handleConnect(commandParts, sc);
        }

        MapMessages message = MapMessages.RESPONSE;
        Player player = users.get(sc);

        if (!player.isAlive()) {
            return YOU_ARE_DEAD;
        }

        if (player.isFightingMob() || player.isFightingPlayer()) {
            if (player.isFighting()) {
                if (enumCommand == UP || enumCommand == DOWN || enumCommand == LEFT || enumCommand == RIGHT) {
                    return CANNOT_FLEE;
                }
                switch (enumCommand) {
                    case NORMAL_ATTACK: message = this.engine.normalAttack(player); break;
                    case SPELL_ATTACK:  message = this.engine.spellAttack(player); break;

                    case SHOW_BACKPACK: return this.actionHandler.showBackpack(player);
                    case USE_POTION:    return this.actionHandler.usePotion(player, receivedMessage);
                    default: return UNKNOWN_COMMAND_FIGHT;
                }
            }

            else {
                startFight(player, enumCommand);
            }
        }
        else {
            switch (enumCommand) {
                case DISCONNECT: return disconnectClient(sc);

                case UP:            message = this.engine.moveUp(player); break;
                case DOWN:          message = this.engine.moveDown(player); break;
                case LEFT:          message = this.engine.moveLeft(player); break;
                case RIGHT:         message = this.engine.moveRight(player); break;

                case USE_POTION:    return this.actionHandler.usePotion(player, receivedMessage);
                case EQUIP_SPELL:   return this.actionHandler.equipSpell(player, receivedMessage);
                case EQUIP_WEAPON:  return this.actionHandler.equipWeapon(player, receivedMessage);
                case SHOW_BACKPACK: return this.actionHandler.showBackpack(player);

                case SHARE_ITEM:    return this.actionHandler.shareItem(player, users, receivedMessage);
                default: return ServerMessages.UNKNOWN_COMMAND;
            }
        }

        // false, because we don't want to stop the users' connections
        this.updateAll(false);

        if (message == YOU_DIED) {
            users.remove(sc);
        }

        RESPONSE_TEXT.setMessage(getEnumMessage(message));
        return RESPONSE_TEXT;
    }

    private ServerMessages handleConnect(String[] commandParts, SocketChannel sc) {
        if (commandParts.length < 2) {
            return MISSING_USERNAME;
        }

        String username = commandParts[1];

        ServerMessages message = newClient(username, sc);

        this.updateAll(false);
        return message;
    }

    private void startFight(Player player, Commands enumCommand) {
        if (enumCommand == CONFIRM_FIGHT_SMALL || enumCommand == CONFIRM_FIGHT_BIG) {
            player.setFightingMode(true);

            if (player.getPlayerAgainst() != null) {
                player.getPlayerAgainst().setFightingMode(true);
            }
        }

        if (enumCommand == DECLINE_FIGHT_SMALL || enumCommand == DECLINE_FIGHT_BIG) {
            Minion minionAgainst = player.getMinionAgainst();
            Player playerAgainst = player.getPlayerAgainst();

            if (minionAgainst != null) {
                player.getMinionAgainst().setFightingMode(false);
                player.setFightMob(null);
                player.setFightingMode(false);
            }

            if (playerAgainst != null) {
                player.setFightPlayer(null);
                playerAgainst.setFightPlayer(null);
                player.setFightingMode(false);
                playerAgainst.setFightingMode(false);
            }

        }
    }

    private ServerMessages newClient(String username, SocketChannel sc) {

        Player newPlayer = new Player(username);

        if (users.containsValue(newPlayer)) {
            return USERNAME_EXISTS;
        }

        if (users.size() >= maxUsers) {
            return SERVER_FULL;
        }

        users.put(sc, newPlayer);
        System.out.printf(getEnumMessage(HAS_CONNECTED), username);

        StringBuilder message = new StringBuilder(getEnumMessage(CONNECTED_OK) + System.lineSeparator());

        if (!engine.addPlayer(newPlayer)) {
            message.append(GameEngineMessages.NO_SPACE);
        }

        RESPONSE_TEXT.setMessage(message.toString());
        return RESPONSE_TEXT;
    }

    private ServerMessages disconnectClient(SocketChannel sc) {

        try {
            sc.close();
        } catch (IOException e) {
            return DISCONNECT_FAIL;
        }

        Player player = users.get(sc);
        users.remove(sc);

        engine.removePlayer(player);
        System.out.printf(getEnumMessage(HAS_DISCONNECTED), player.getName());
        return DISCONNECT_OK;

    }

    private void updateAll(boolean kill) {

        String currentMap = this.engine.getMap().getCurrentMapAsString();

        // users is not null
        for (SocketChannel sc : users.keySet()) {
            if (kill) {
                update(getEnumMessage(STOP_ALL_CLIENTS), sc);
            }
            else {
                update(currentMap, sc);
            }
        }

    }

    private void update(String currentMap, SocketChannel socketTo) {
        try {
            commandBuffer.clear();
            commandBuffer.put(currentMap.getBytes());
            commandBuffer.put(System.lineSeparator().getBytes());

            commandBuffer.flip();
            while (commandBuffer.hasRemaining()) {
                socketTo.write(commandBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // if the server shutdowns - send a proper message to all currently connected users and disconnect them
    private void killClients() {
        updateAll(true);

        HashSet<SocketChannel> tempSet = new HashSet<>(users.keySet());
        for (SocketChannel sc : tempSet) {
            disconnectClient(sc);
        }
    }

    private void stop() {
        try {
            this.runServer = false;

            this.killClients();

            final int second = 1000;
            Thread.sleep(second);

            System.out.printf(getEnumMessage(SOCKETS_REMAINING), users.size());
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        this.stop();

        this.selector.close();
        commandBuffer.clear();
        this.serverSocketChannel.close();
    }

    public static String getEnumMessage(Enum message) {
        return message.toString();
    }

    public static void notifyPlayer(Player playerTo, String message) {
        try {
            SocketChannel socketTo = null;

            for (Map.Entry<SocketChannel, Player> user : users.entrySet()) {
                if (user.getValue() == playerTo) {
                    socketTo = user.getKey();
                    break;
                }
            }

            commandBuffer.clear();
            commandBuffer.put(message.getBytes());
            commandBuffer.put(System.lineSeparator().getBytes());
            commandBuffer.put(System.lineSeparator().getBytes());

            commandBuffer.flip();
            while (commandBuffer.hasRemaining()) {

                // a socketTo will always be found
                if (socketTo != null) {
                    socketTo.write(commandBuffer);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        final int serverPort = 8080;

        String mapFilePath = "resources/default-map.txt";

        try (GameServer es = new GameServer(serverPort, mapFilePath)) {
            es.start();
        } catch (IOException e) {
            System.err.println(CANNOT_START);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(CANNOT_CLOSE);
            e.printStackTrace();
        }
    }
}