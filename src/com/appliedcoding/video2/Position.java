package com.appliedcoding.video2;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            return true;
        }

        if (obj == null || !(obj instanceof Position)) {
            return false;
        }

        Position other = (Position) obj;
        return x == other.getX() && y == other.getY();
    }

    // See: https://stackoverflow.com/questions/919612/mapping-two-integers-to-one-in-a-unique-and-deterministic-way
    @Override
    public int hashCode() {
        return (x + y) * (x + y + 1) / 2 + y;
    }
}
