package com.appliedcoding.video2;

import java.util.ArrayList;
import java.util.List;

enum Direction {
    Up, Down, Left, Right
}

public class Snake {

    private Direction direction = Direction.Right;
    private List<Position> body = new ArrayList<>();
    private int grow;

    public Snake(Position head) {
        body.add(head);
        grow(20);
    }

    public void move() {
        Position head = getHead();
        Position newHead;
        switch (direction) {
            case Up: //up
                newHead = new Position(head.getX(), head.getY() - 1);
                break;

            case Down: //down
                newHead = new Position(head.getX(), head.getY() + 1);
                break;

            default:
            case Right: //right
                newHead = new Position(head.getX() + 1, head.getY());
                break;

            case Left: //left
                newHead = new Position(head.getX() - 1, head.getY());
                break;
        }

        body.add(0, newHead);

        if (grow > 0) {
            grow--;
        } else {
            body.remove(body.size() - 1);
        }
    }

    public void grow(int amount) {
        grow += amount;
    }

    public boolean isGrowing() {
        return grow > 0;
    }

    public void setDirection(Direction newDirection) {
        switch (direction) {
            case Up:
                if (newDirection != Direction.Down) {
                    direction = newDirection;
                }
                break;

            case Down:
                if (newDirection != Direction.Up) {
                    direction = newDirection;
                }
                break;

            case Right:
                if (newDirection != Direction.Left) {
                    direction = newDirection;
                }
                break;

            case Left:
                if (newDirection != Direction.Right) {
                    direction = newDirection;
                }
                break;
        }
    }

    public Position getHead() {
        return body.get(0);
    }

    public Position getTail() {
        return body.get(body.size() - 1);
    }

    public boolean isEatingItself() {
        return body.lastIndexOf(getHead()) > 0;
    }

    public List<Position> getBody() {
        return body;
    }

    public void paintRemove(Console console) {
        if (!isGrowing()) {
            Position tail = getTail();
            console.printAt(" ", tail.getX(), tail.getY());
        }
    }

    public void paint(Console console) {
        Position head = getHead();
        console.setTextColor(Console.ANSI_YELLOW);
        console.printAt("\u2588", head.getX(), head.getY());
    }
}
