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
}
