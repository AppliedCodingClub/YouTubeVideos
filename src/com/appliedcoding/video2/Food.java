package com.appliedcoding.video2;

public class Food {

    private final Position position;
    private final char label;

    public Food(Position position, char label) {
        this.position = position;
        this.label = label;
    }

    public Position getPosition() {
        return position;
    }

    public char getLabel() {
        return label;
    }

    public void paint(Console console) {
        console.setTextColor(Console.ANSI_GREEN);
        console.printAt(String.valueOf(label), position.getX(), position.getY());
    }
}
