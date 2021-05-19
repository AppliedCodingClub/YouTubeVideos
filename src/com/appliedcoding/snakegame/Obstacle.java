package com.appliedcoding.snakegame;

import java.util.HashSet;
import java.util.Set;

public class Obstacle {
    private Set<Position> body;

    public Obstacle() {
        body = new HashSet<>();
    }

    public void paint(Console console) {
        console.setTextColor(Console.ANSI_SALMON_RED);
        for (Position position : body) {
            String s;
            int x = position.getX();
            int y = position.getY();
            if (y % 2 == 1) { // odd line --> upper
                if (body.contains(new Position(x, y + 1))) { // is obstacle below?
                    s = "\u2588"; // full block █
                } else {
                    s = "\u2580"; // upper half ▀
                }
            } else { // even line --> lower
                if (body.contains(new Position(x, y - 1))) { // is obstacle above?
                    s = "\u2588"; // full block █
                } else {
                    s = "\u2584"; // lower half ▄
                }
            }

            console.printAt(s, x, (int) Math.round(y / 2.0));
        }
    }

    public void addLine(Position start, Position end) {
        int startX = start.getX();
        int endX = end.getX();
        int distX = endX - startX;

        int startY = start.getY();
        int endY = end.getY();
        int distY = endY - startY;

        int steps = Math.max(Math.abs(distX), Math.abs(distY));
        float stepX = (float) distX / (float) steps;
        float stepY = (float) distY / (float) steps;

        double x = startX;
        double y = startY;

        for (int i = 0; i <= steps; i++) {
            Position position = new Position((int) Math.round(x), (int) Math.round(y));
            body.add(position);

            x += stepX;
            y += stepY;
        }
    }

    public void addLineMirrorH(Position start, Position end, int maxX) {
        int startX = start.getX();
        int endX = end.getX();
        int distX = endX - startX;

        int startY = start.getY();
        int endY = end.getY();
        int distY = endY - startY;

        int steps = Math.max(Math.abs(distX), Math.abs(distY));
        float stepX = (float) distX / (float) steps;
        float stepY = (float) distY / (float) steps;

        double x = startX;
        double y = startY;

        for (int i = 0; i <= steps; i++) {
            int screenX = (int) Math.round(x);
            int screenY = (int) Math.round(y);
            body.add(new Position(screenX, screenY));
            body.add(new Position(maxX - screenX + 1, screenY));

            x += stepX;
            y += stepY;
        }
    }

    public boolean contains(Position position) {
        return body.contains(position);
    }
}
