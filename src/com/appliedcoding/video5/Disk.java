package com.appliedcoding.video5;

public class Disk {

    private int size;
    private int x;
    private int y;

    public Disk(int size) {
        this.size = size;
    }

    public int getWidth() {
        return 4 * size + 1;
    }

    public int getHeight() {
        return 1;
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
}
