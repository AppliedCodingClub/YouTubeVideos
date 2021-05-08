package com.appliedcoding.video2;

import java.util.ArrayList;
import java.util.List;

enum Direction {
    Up, Down, Left, Right
}

public class Snake {

    private List<Position> snake = new ArrayList<>();
    private Direction direction = Direction.Right;
    private int grow;

    public Snake(Position head) {
        snake.add(head);
    }

    public void move() {
        Position head = getHead();
        Position newHead;
        switch (direction) {
            case Up:
                newHead = new Position(head.getX(), head.getY() - 1);
                break;

            case Down:
                newHead = new Position(head.getX(), head.getY() + 1);
                break;

            case Left:
                newHead = new Position(head.getX() - 1, head.getY());
                break;

            case Right:
            default:
                newHead = new Position(head.getX() + 1, head.getY());
                break;
        }
        snake.add(0, newHead);

        if (grow > 0) {
            grow--;
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    public boolean isEatingItself() {
        Position head = null;
        for (Position position : snake) {
            if (head == null) {
                head = position;
            } else if (head.equals(position)) {
                return true;
            }
        }

        return false;
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

    public void grow(int amount) {
        grow += amount;
    }

    public List<Position> getBody() {
        return snake;
    }

    public Position getHead() {
        return snake.get(0);
    }

    public Position getNeck() {
        return snake.get(1);
    }

    public Position getTail() {
        return snake.get(snake.size() - 1);
    }
}