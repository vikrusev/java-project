package server;

import messages.map.MapMessages;
import messages.player.BackpackMessages;
import participants.actor.Position;
import participants.actor.essentials.Treasure;
import participants.actor.monster.Minion;
import participants.actor.player.Player;
import server.services.engine.GameMap;

import static messages.map.MapMessages.*;
import static messages.player.BackpackMessages.ITEM_PICKED;
import static server.GameServer.getEnumMessage;
import static server.GameServer.notifyPlayer;

public class GameEngine {

    private GameMap gameMap;

    public GameEngine(String mapFileName) {
        this.gameMap = new GameMap(mapFileName);
    }

    public GameMap getMap() {
        return this.gameMap;
    }

    boolean addPlayer(Player player) {
        return this.gameMap.addPlayer(player);
    }

    void removePlayer(Player player) {
        int playerIndex = Character.getNumericValue(player.getIndex());

        // free the index
        Player.getPlayerIndices()[playerIndex - 1] = false;

        this.gameMap.removePlayer(player);
    }

    private void removeMonster(Minion minion) {
        this.gameMap.removeMinion(minion);
        this.gameMap.addNewRandomMinion();
    }

    MapMessages moveUp(Player player) {
        return move(player, -1, 0);
    }

    MapMessages moveDown(Player player) {
        return move(player, 1, 0);
    }

    MapMessages moveLeft(Player player) {
        return move(player, 0, -1);
    }

    MapMessages moveRight(Player player) {
        return move(player, 0, 1);
    }

    private synchronized MapMessages move(Player player, int x, int y) {
        Position currentPlayerPosition = player.getPosition();
        x += currentPlayerPosition.getX();
        y += currentPlayerPosition.getY();

        MapMessages message = checkMove(x, y);

        if (message == MOVE_OK) {
            return this.gameMap.movePlayer(player, x, y);
        }

        if (message == TREASURE) {
            this.gameMap.movePlayer(player, x, y);
            message = dealWithTreasure(player, x, y);
        }

        if (message == MONSTER) {
            Minion minion = this.gameMap.findMinion(x, y);

            RESPONSE_TEXT.setMessage(String.format(getEnumMessage(message), minion.getLevel()));

            if (!minion.isFighting()) {
                player.setFightMob(minion);
                minion.setFightingMode(true);
            }
        }

        else if (message == PLAYER) {
            Player playerAgainst = this.gameMap.findPlayer(x, y);


            if (playerAgainst != null && !playerAgainst.isFighting()) {
                RESPONSE_TEXT.setMessage(String.format(getEnumMessage(message), player.getHealth()));
                playerAgainst.setFightPlayer(player);
                playerAgainst.setFightingMode(true);
                player.setFightPlayer(playerAgainst);
            }
            else {
                RESPONSE_TEXT.setMessage(getEnumMessage(PLAYER_ALREADY_FIGHTING));
            }

        }

        else {
            return message;
        }

        return RESPONSE_TEXT;
    }

    private MapMessages checkMove(int x, int y) {
        return this.gameMap.checkPosition(x, y);
    }

    private MapMessages dealWithTreasure(Player player, int x, int y) {
        BackpackMessages backpackMessage = Treasure.randomTreasure().collect(player);
        String response = getEnumMessage(backpackMessage);

        if (backpackMessage == ITEM_PICKED) {
            this.gameMap.removeTreasure(x, y);
            this.gameMap.addNewRandomTreasure();
        }

        RESPONSE_TEXT.setMessage(response);
        return RESPONSE_TEXT;
    }

    MapMessages normalAttack(Player player) {
        int dealDamage = player.normalAttack();
        return attack(player, dealDamage, true);
    }

    MapMessages spellAttack(Player player) {
        boolean isNormalAttack = false;
        int dealDamage = player.spellAttack();

        if (dealDamage <= -1) {
            isNormalAttack = true;
            dealDamage = player.normalAttack();
        }

        return attack(player, dealDamage, isNormalAttack);
    }

    private MapMessages attack(Player player, int dealDamage, boolean isNormalAttack) {

        if (player.isFightingMob()) {
            return mobFight(player, dealDamage, isNormalAttack);
        }

        if (player.isFightingPlayer()) {
            return playerFight(player, dealDamage, isNormalAttack);
        }

        return FIGHT_NOBODY;
    }

    private MapMessages mobFight(Player player, int dealDamage, boolean isNormalAttack) {
        Minion minion = player.getMinionAgainst();
        dealDamage = Math.max(1, dealDamage - minion.getDefense() / 4);
        minion.takeDamage(dealDamage);

        if (!minion.isAlive()) {
            int experienceGained = minion.getExperience();
            removeMonster(minion);

            player.takeExperience(experienceGained);
            player.endFight();

            RESPONSE.setMessage(String.format(getEnumMessage(KILLED_MINION), experienceGained));
            return RESPONSE;
        }

        int takenDamage = minion.normalAttack() - player.getDefense();
        player.takeDamage(takenDamage);

        if (!player.isAlive()) {
            Treasure droppedItem = player.dropRandomItem();
            removePlayer(player);

            if (droppedItem != null) {
                this.gameMap.addNewFixedTreasure(player.getPosition().getX(), player.getPosition().getY());
            }

            return YOU_DIED;
        }

        String response = getEnumMessage(SPELL_ATTACK_MINION);
        if (isNormalAttack) {
            response = getEnumMessage(NORMAL_ATTACK_MINION);
        }

        RESPONSE.setMessage(String.format(response, dealDamage, minion.getHealth(), takenDamage, player.getHealth()));
        return RESPONSE;
    }

    private MapMessages playerFight(Player player, int dealDamage, boolean isNormalAttack) {
        Player playerAgainst = player.getPlayerAgainst();
        dealDamage = Math.max(1, dealDamage - playerAgainst.getDefense() / 4);
        playerAgainst.takeDamage(dealDamage);

        if (!playerAgainst.isAlive()) {

            // winner takes experience equal to 20 times the level of the loser
            int experienceGained = playerAgainst.getLevel() * 20;
            removePlayer(playerAgainst);

            player.takeExperience(experienceGained);
            player.endFight();

            Treasure droppedItem = playerAgainst.dropRandomItem();
            removePlayer(playerAgainst);

            if (droppedItem != null) {
                this.gameMap.addNewFixedTreasure(playerAgainst.getPosition().getX(), playerAgainst.getPosition().getY());
            }

            notifyPlayer(playerAgainst, getEnumMessage(YOU_DIED));

            RESPONSE.setMessage(String.format(getEnumMessage(KILLED_PLAYER), experienceGained));
            return RESPONSE;
        }

        notifyPlayer(playerAgainst, String.format(getEnumMessage(HIT_YOU),
                                            player.getIndex(), dealDamage, playerAgainst.getHealth()));

        String response = getEnumMessage(NORMAL_ATTACK_PLAYER);
        if (isNormalAttack) {
            response = getEnumMessage(SPELL_ATTACK_PLAYER);
        }

        RESPONSE.setMessage(String.format(response, dealDamage, playerAgainst.getHealth()));
        return RESPONSE;
    }

}
