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

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Position)) {
            return false;
        }

        Position other = (Position) obj;
        return getX() == other.getX() && getY() == other.getY();
    }

    @Override
    public String toString() {
        return String.format("Position[%d,%d]", x, y);
    }
}
