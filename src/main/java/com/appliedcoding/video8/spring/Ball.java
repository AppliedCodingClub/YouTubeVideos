package com.appliedcoding.video8.spring;

import com.appliedcoding.io.CanvasBase;

public class Ball {

    public static final Vector GRAVITY = new Vector(0, 9.8f); // m/s^2

    private String id;
    private Vector position;
    private Vector acceleration;
    private Vector velocity;
    private float mass;
    private boolean locked;
    private Vector lastAcceleration;
    private boolean gravityApplied;
    private float time;
    private int color;

    public Ball(float x, float y, float mass, String id, int color) {
        position = new Vector(x, y);
        this.mass = mass;
        this.id = id;
        this.color = color;
        acceleration = Vector.NULL;
        velocity = Vector.NULL;
    }

    // F = m * a
    // a = F / m
    public void applyForce(Vector force) {
        Vector acceleration = force.divide(mass);
        applyAcceleration(acceleration);
    }

    public void applyGravity() {
        if (!gravityApplied) {
            applyAcceleration(GRAVITY);
            gravityApplied = true;
        }
    }

    public void applyAcceleration(Vector acceleration) {
        lastAcceleration = this.acceleration;
        this.acceleration = this.acceleration.add(acceleration);
    }

    // v = v0 + a * t  ==>  v += a * dt
    // p = p0 + v * t  ==>  p += v * dt
    public void update(float time) {
        float dt = time - this.time;
        if (dt == 0) {
            return;
        }

        if (locked) {
            velocity = Vector.NULL;
        } else {
            Vector da = acceleration.multiply(dt);
            velocity = velocity.add(da);
            Vector dv = velocity.multiply(dt);
            position = position.add(dv);

            float att = 1 - dt * 0.1f; // attenuation 10% of dt
            velocity = velocity.multiply(att);
        }

        lastAcceleration = acceleration;
        acceleration = Vector.NULL;
        gravityApplied = false;
        this.time = time;
    }

    public void show(CanvasBase canvas) {
        if (color >= 0) {
            canvas.setColor(color);
        }

//        canvas.midptellipse(getX(), getY(), 1, 1);
        canvas.plot(getX(), getY());
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public float getX() {
        return position.getSizeX();
    }

    public float getY() {
        return position.getSizeY();
    }

    public void setY(float y) {
        position = new Vector(position.getSizeX(), y);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return String.format("%s p:%s v:%s a:%s", id, position, velocity, lastAcceleration);
    }
}
