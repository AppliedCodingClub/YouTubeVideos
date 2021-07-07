package com.appliedcoding.video8.spring;

public class Vector {

    public static final Vector NULL = new Vector(0, 0);

    private final float sizeX;
    private final float sizeY;

    public Vector(float sizeX, float sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public Vector add(Vector other) {
        return new Vector(sizeX + other.sizeX, sizeY + other.sizeY);
    }

    public Vector subtract(Vector other) {
        return new Vector(sizeX - other.sizeX, sizeY - other.sizeY);
    }

    public Vector multiply(float scalar) {
        return new Vector(sizeX * scalar, sizeY * scalar);
    }

    public Vector divide(float scalar) {
        return new Vector(sizeX / scalar, sizeY / scalar);
    }

    public float getMagnitude() {
        return (float) Math.sqrt(sizeX * sizeX + sizeY * sizeY);
    }

    public Vector normalize() {
        float magnitude = getMagnitude();
        return new Vector(sizeX / magnitude, sizeY / magnitude);
    }

    public float getSizeX() {
        return sizeX;
    }

    public float getSizeY() {
        return sizeY;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", sizeX, sizeY);
    }
}
