package com.appliedcoding.video2;

public class Food {

    private Position position;
    private char label;

    public Food(int x, int y, char label) {
        position = new Position(x, y);
        this.label = label;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public char getLabel() {
        return label;
    }

    public void setLabel(char label) {
        this.label = label;
    }
}
