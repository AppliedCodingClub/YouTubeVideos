package com.appliedcoding.snakegame.model;

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

    @Override
    public String toString() {
        return String.format("Food[%s, %s, %s]", label, position.get(0), position.get(1));
    }

    public char getLabel() {
        return label;
    }

    public char getNextLabel() {
        if (label == '9') {
            return 'A';
        } else if (label == 'Z') {
            return 'a';
        } else if (label == 'z') {
            return '0';
        } else {
            return (char) (label + 1);
        }
    }
}
