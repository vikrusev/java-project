package messages.map;

import java.util.HashSet;

public enum MapObjects {

    MONSTER('M'), TREASURE('T'), OBSTACLE('#'), PATH('.');

    private char symbol;

    MapObjects(char symbol) {
        this.symbol = symbol;
    }

    public char toCharacter() {
        return this.symbol;
    }

    public static HashSet<Character> getAllSymbols() {
        HashSet<Character> symbols = new HashSet<>();

        for (MapObjects object : MapObjects.values()) {
            symbols.add(object.toCharacter());
        }

        return symbols;
    }

}
