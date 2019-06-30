package bg.sofia.uni.fmi.mjt.dungeons.server.services.server;

import bg.sofia.uni.fmi.mjt.dungeons.messages.player.BackpackMessages;
import bg.sofia.uni.fmi.mjt.dungeons.messages.server.ServerMessages;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.Position;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.Treasure;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.arsenal.Spell;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.arsenal.Weapon;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.potions.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.essentials.potions.ManaPotion;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Backpack;
import bg.sofia.uni.fmi.mjt.dungeons.participants.actor.player.Player;
import com.google.gson.Gson;

import java.nio.channels.SocketChannel;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.dungeons.messages.server.ServerMessages.*;
import static bg.sofia.uni.fmi.mjt.dungeons.messages.player.BackpackMessages.*;
import static bg.sofia.uni.fmi.mjt.dungeons.server.GameServer.getEnumMessage;
import static bg.sofia.uni.fmi.mjt.dungeons.server.GameServer.notifyPlayer;
import static java.util.Map.Entry;

public class ActionHandler {

    private Treasure getItem(Player player, String receivedMessage) {
        String[] lineParts = receivedMessage.split(" ");
        int itemIndex = Integer.parseInt(lineParts[1]);

        return player.getBackpack().getSingleItem(itemIndex - 1);
    }

    public ServerMessages usePotion(Player player, String receivedMessage) {
        Treasure item = getItem(player, receivedMessage);

        if (item instanceof HealthPotion) {
            int healWith = ((HealthPotion) item).healingPoints();
            player.takeHealing(healWith);

            return USED_POTION;
        }

        if (item instanceof ManaPotion) {
            int replenishWith = ((ManaPotion) item).replenishPoints();
            player.takeMana(replenishWith);

            return USED_POTION;
        }

        return ITEM_NOT_POTION;
    }

    public ServerMessages equipSpell(Player player, String receivedMessage) {
        Treasure item = getItem(player, receivedMessage);

        if (item instanceof Spell) {
            player.equipSpell((Spell) item);

            return SPELL_EQUIPPED;
        }

        return ITEM_NOT_SPELL;
    }

    public ServerMessages equipWeapon(Player player, String receivedMessage) {
        Treasure item = getItem(player, receivedMessage);

        if (item instanceof Weapon) {
            player.equipWeapon((Weapon) item);

            return WEAPON_EQUIPPED;
        }

        return ITEM_NOT_WEAPON;
    }

    public ServerMessages showBackpack(Player player) {
        Backpack backpack = player.getBackpack();

        if (backpack == null || backpack.getSize() == 0) {
            RESPONSE_TEXT.setMessage(getEnumMessage(BACKPACK_EMPTY));
            return RESPONSE_TEXT;
        }

        String backpackJson = player.getBackpack().getItemsAsString();

        RESPONSE_TEXT.setMessage(backpackJson);
        return RESPONSE_TEXT;
    }

    public ServerMessages shareItem(Player playerFrom, Map<SocketChannel, Player> users, String receivedMessage) {

        try {
            String[] lineParts = receivedMessage.split(" ");

            String playerIndex = lineParts[1];

            Entry<SocketChannel, Player> socketPlayer = getPlayerByIndex(playerIndex, users);
            Player playerTo = socketPlayer.getValue();

            Position positionFrom = playerFrom.getPosition();
            Position positionTo = playerTo.getPosition();

            int xDifference = Math.abs(positionFrom.getX() - positionTo.getX());
            int yDifference = Math.abs(positionFrom.getY() - positionTo.getY());

            // players are too far away
            if (xDifference > 1 || yDifference > 1) {
                return PLAYER_NOT_NEAR;
            }

            if (playerTo.getBackpack().isFull()) {
                return OTHER_BACKPACK_FULL;
            }

            int itemIndexToShare = Integer.parseInt(lineParts[2]);
            Treasure itemToShare = playerFrom.getBackpack().getSingleItem(itemIndexToShare - 1);

            if (itemToShare != null) {
                BackpackMessages backpackMessage = playerFrom.getBackpack().remove(itemIndexToShare - 1);

                if (backpackMessage == ITEM_NOT_FOUND) {
                    RESPONSE_TEXT.setMessage(getEnumMessage(backpackMessage));
                    return RESPONSE_TEXT;
                }

                Gson gson = new Gson();
                String message = String.format(getEnumMessage(HAS_GIVEN),
                                                playerFrom.getIndex(), gson.toJson(itemToShare));

                notifyPlayer(playerTo, message);
                itemToShare.collect(playerTo);

                return SHARE_SUCCESS;
            }

            RESPONSE_TEXT.setMessage(getEnumMessage(ITEM_NOT_FOUND));
            return RESPONSE_TEXT;

        } catch (Exception e) {
            RESPONSE_TEXT.setMessage(e.getMessage());
            return RESPONSE_TEXT;
        }
    }

    private Entry<SocketChannel, Player> getPlayerByIndex(String playerIndex, Map<SocketChannel, Player> users)
                                        throws Exception
    {
        if (playerIndex.length() == 1) {

            char index = playerIndex.charAt(0);

            for (Entry<SocketChannel, Player> user : users.entrySet()) {
                if (user.getValue().getIndex() == index) {
                    return user;
                }
            }
        }

        throw new Exception(getEnumMessage(PLAYER_NOT_FOUND));
    }
}
