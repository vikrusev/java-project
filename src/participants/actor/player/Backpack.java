package participants.actor.player;

import messages.player.BackpackMessages;
import participants.essentials.Treasure;
import com.google.gson.Gson;

import java.util.ArrayList;

import static messages.player.BackpackMessages.*;

public class Backpack {

    private ArrayList<Treasure> items = new ArrayList<>();
    private int capacity;

    public Backpack() {
        this(10);
    }

    public Backpack(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Adds a treasure to the backpack if there is space.
     *
     * @param treasure - the treasure to be added to teh backpack.
     * @return BackpackMessages - a mesage from the backpack.
     */
    BackpackMessages add(Treasure treasure) {
        // the backpack is full
        if (isFull()) {
            return BACKPACK_FULL;
        }

        // otherwise we add the item
        this.items.add(treasure);

        return ITEM_PICKED;
    }

    /**
     * Removes a treasure from the backpack.
     * @param index - the treasure to be removed.
     * @return BackpackMessages - a message from the backpack.
     */
    public BackpackMessages remove(int index) {
        try {
            // if item with this VALID index exists
            if (this.items.remove(index) != null) {

                return ITEM_REMOVED;
            }

            return ITEM_NOT_FOUND;

        } catch (IndexOutOfBoundsException e) {
            return ITEM_NOT_FOUND;
        }
    }

    /**
     * @param index - the index of the treasure to be taken.
     * @return Treasure - the desired treasure, if exists.
     */
    public Treasure getSingleItem(int index) {
        try {
            return this.items.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Creates a list of all items in the backpack.
     * This method is using GSON to handle JSON.
     * @return String - the complete list of all items in the backpack.
     */
    public String getItemsAsString() {

        Gson gson = new Gson();
        StringBuilder itemsAsString = new StringBuilder();

        int counter = 1;
        for (Treasure treasure : this.items) {

            String treasureJson = gson.toJson(treasure);

            itemsAsString.append("*").append(counter).append(": ");
            itemsAsString.append(treasureJson);
            itemsAsString.append(System.lineSeparator());

            ++counter;
        }

        return itemsAsString.toString();
    }

    public int getSize() {
        return this.items.size();
    }

    public boolean isFull() {
        return this.items.size() >= this.capacity;
    }

}
