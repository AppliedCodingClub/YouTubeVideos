package com.appliedcoding.snakegame;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Position)) {
            return false;
        }

        Position other = (Position) obj;
        return getX() == other.getX() && getY() == other.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // See: https://stackoverflow.com/questions/919612/mapping-two-integers-to-one-in-a-unique-and-deterministic-way
    // https://stackoverflow.com/questions/892618/create-a-hashcode-of-two-numbers
    // https://stackoverflow.com/questions/11742593/what-is-the-hashcode-for-a-custom-class-having-just-two-int
    // -properties
    @Override
    public int hashCode() {
        return (x + y) * (x + y + 1) / 2 + y;
    }

    @Override
    public String toString() {
        return String.format("Position[%d,%d]", x, y);
    }
}
