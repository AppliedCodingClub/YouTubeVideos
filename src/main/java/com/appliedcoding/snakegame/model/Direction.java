package com.appliedcoding.snakegame.model;

public enum Direction {
    Up(0), // North
    Right(1), // East
    Down(2), // South
    Left(3); // West

    private int value;

    Direction(int value) {
        this.value = value;
    }

    public static Direction fromInt(int value) {
        return values()[value];
    }

    public int getValue() {
        return value;
    }

    public Direction rightOf() {
        return fromInt((value + 1) % 4);
    }

    public Direction oppositeOf() {
        return fromInt((value + 2) % 4);
    }

    public Direction leftOf() {
        return fromInt((value + 3) % 4);
    }
}
