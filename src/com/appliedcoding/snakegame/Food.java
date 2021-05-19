package com.appliedcoding.snakegame;

import java.util.Arrays;
import java.util.List;

public class Food {

    private final List<Position> position;
    private final char label;

    public Food(Position positionUpper, Position positionLower, char label) {
        position = Arrays.asList(positionUpper, positionLower);
        this.label = label;
    }

    public List<Position> getPosition() {
        return position;
    }

    public char getLabel() {
        return label;
    }

    public void paint(Console console) {
        console.setTextColor(Console.ANSI_GREEN);
        console.printAt(String.valueOf(label), position.get(0).getX(), (int) (position.get(1).getY() / 2.0));
    }
}
