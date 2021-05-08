package com.appliedcoding.video2;

import com.appliedcoding.video1.Console;

import java.util.HashSet;
import java.util.Set;

public class Obstacle {

    private Set<Position> obstacle = new HashSet<>();

    public void addLine(Position a, Position b) {
        double aX = a.getX();
        double aY = a.getY();
        double bX = b.getX();
        double bY = b.getY();

        int delta = (int) Math.max(Math.abs(aX - bX), Math.abs(aY - bY));
        double deltaX = (bX - aX) / (double) delta;
        double deltaY = (bY - aY) / (double) delta;

        for (int i = 0; i <= delta; i++) {
            Position position = new Position((int) aX, (int) aY);
            obstacle.add(position);

            aX += deltaX;
            aY += deltaY;
        }
    }

    public boolean isCollision(Position head) {
        return obstacle.contains(head);
    }

    public Set<Position> getObstacle() {
        return obstacle;
    }

    public void paint(Console console) {
        for (Position position : obstacle) {
            console.putCharAt('X', position.getY(), position.getX());
        }
    }
}
