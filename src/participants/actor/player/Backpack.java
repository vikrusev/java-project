package participants.actor.player;

import messages.player.BackpackMessages;
import participants.actor.essentials.Treasure;
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

    BackpackMessages add(Treasure treasure) {
        // the backpack is full
        if (isFull()) {
            return BACKPACK_FULL;
        }

        // otherwise we add the item
        this.items.add(treasure);

        return ITEM_PICKED;
    }

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

    public ArrayList<Treasure> getItems() {
        return this.items;
    }

    public int getSize() {
        return this.items.size();
    }

    public Treasure getSingleItem(int index) {
        try {
            return this.items.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

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

    public boolean isFull() {
        return this.items.size() >= this.capacity;
    }

}
