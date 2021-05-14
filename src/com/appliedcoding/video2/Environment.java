package com.appliedcoding.video2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Environment {

    private final Position topLeft;
    private final Position bottomRight;
    private final Snake snake;
    private Food food;
    private List<Obstacle> obstacles;

    public Environment(Position topLeft, Position bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        snake = new Snake(new Position(bottomRight.getX() / 2, bottomRight.getY() / 2));
        createObstacles();
        addFood();
    }

    private void createObstacles() {
        obstacles = new ArrayList<>();
        Obstacle obstacle = new Obstacle();
        addObstacle(obstacle);
        obstacle.addLine(new Position(4, 4), new Position(40, 9));
        obstacle.addLine(new Position(40, 9), new Position(76, 4));
        obstacle.addLine(new Position(5, 11), new Position(74, 11));
        obstacle.addLine(new Position(15, 17), new Position(30, 17));
        obstacle.addLine(new Position(50, 17), new Position(65, 17));
        obstacle.addLine(new Position(40, 13), new Position(40, 22));
    }

    public void paintBackground(Console console) {
        console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
        char[] chars = new char[bottomRight.getX()];
        Arrays.fill(chars, ' ');
        String line = new String(chars);
        for (int y = 1; y <= bottomRight.getY(); y++) {
            console.printAt(line, 1, y);
        }
    }

    public void paint(Console console) {
        snake.paint(console);

        console.setTextColor(Console.ANSI_BRIGHT_RED);
        for (Obstacle obstacle : obstacles) {
            obstacle.paint(console);
        }

        food.paint(console);
    }

    public void paintRemove(Console console) {
        snake.paintRemove(console);
    }

    public GameState checkEvent() {
        GameState result = null;

        if (checkCollision()) {
            result = new GameState(true, false);
        }

        if (hasFoundFood()) {
            if (getFood().getLabel() == '9') {
                result = new GameState(false, true);
            } else {
                addFood();
                snake.grow(10);
            }
        }

        if (result == null) {
            result = new GameState(false, false);
        }

        return result;
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    public boolean isOutOfBounds() {
        Position head = snake.getHead();
        int x = head.getX();
        int y = head.getY();

        return x < topLeft.getX() || x > bottomRight.getX() || y < topLeft.getY() || y > bottomRight.getY();
    }

    public boolean checkCollision() {
        return isOutOfBounds() || snake.isEatingItself() || isObstacleCollision();
    }

    private boolean isObstacleCollision() {
        Position head = snake.getHead();
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getBody().contains(head)) {
                return true;
            }
        }

        return false;
    }

    public void addFood() {
        Position position;
        boolean isLoop;
        do {
            position = new Position(
                    (int) (topLeft.getX() + Math.random() * bottomRight.getX()),
                    (int) (topLeft.getY() + Math.random() * bottomRight.getY()));
            isLoop = snake.getBody().contains(position);
            if (!isLoop) {
                for (Obstacle obstacle : obstacles) {
                    if (obstacle.getBody().contains(position)) {
                        isLoop = true;
                        break;
                    }
                }
            }
        } while (isLoop);

        char label;
        if (food == null) {
            label = '1';
        } else {
            label = (char) (food.getLabel() + 1);
        }

        food = new Food(position, label);
    }

    public Food getFood() {
        return food;
    }

    public boolean hasFoundFood() {
        return snake.getHead().equals(food.getPosition());
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void calculateNextState() {
        snake.move();
    }

    public Snake getSnake() {
        return snake;
    }
}
