package com.appliedcoding.video2;

import java.util.ArrayList;
import java.util.List;

public class Obstacle {
    private List<Position> body;

    public Obstacle() {
        body = new ArrayList<>();
    }

    public void addLine(Position start, Position end) {
        int startX = start.getX();
        int endX = end.getX();

        int startY = start.getY();
        int endY = end.getY();

        int distX = endX - startX;
        int distY = endY - startY;

        int dist = Math.max(Math.abs(distX), Math.abs(distY));
        double deltaX = (double) distX / (double) dist;
        double deltaY = (double) distY / (double) dist;

        double x = startX;
        double y = startY;

        for (int i = 0; i <= dist; i++) {
            Position position = new Position((int) Math.round(x), (int) Math.round(y));
            body.add(position);

            x += deltaX;
            y += deltaY;
        }
    }

    public List<Position> getBody() {
        return body;
    }

    public void paint(Console console) {
        for (Position position : body) {
            console.printAt("\u2588", position.getX(), position.getY());
        }
    }
}
