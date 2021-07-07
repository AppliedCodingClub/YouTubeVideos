package com.appliedcoding.snakegame.model;

import com.appliedcoding.io.Position;

import java.util.ArrayList;
import java.util.List;

public class Snake {

    private Direction direction;
    private List<Position> body;
    private int grow;

    public Snake(Position head) {
        direction = Direction.Right;
        body = new ArrayList<>();
        body.add(head);
        grow(3);
    }

    public boolean contains(Position position) {
        return body.contains(position);
    }

    public void grow(int amount) {
        grow += amount;
    }

    public boolean isGrowing() {
        return grow > 0;
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

        body.add(0, newHead); // insert new head on top of the body

        if (grow > 0) { // is snake growing?
            grow--; // then don't remove its tail
        } else { // otherwise remove the tip of its tail
            body.remove(body.size() - 1);
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

    public boolean isSnakeAt(Position position) {
        return body.contains(position);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction newDirection) {
        switch (direction) {
            case Up:
//                if (newDirection != Direction.Down) {
                direction = newDirection;
//                }
                break;

            case Down:
//                if (newDirection != Direction.Up) {
                direction = newDirection;
//                }
                break;

            case Right:
//                if (newDirection != Direction.Left) {
                direction = newDirection;
//                }
                break;

            case Left:
//                if (newDirection != Direction.Right) {
                direction = newDirection;
//                }
                break;
        }
    }
}
