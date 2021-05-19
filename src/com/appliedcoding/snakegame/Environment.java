package com.appliedcoding.snakegame;

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

        float minX = topLeft.getX();
        float maxX = bottomRight.getX();
        float minY = topLeft.getY();
        float maxY = bottomRight.getY();
        float distX = maxX - minX;
        float distY = maxY - minY;
        float halfX = distX / 2f;
        float halfY = distY / 2f;
        float quarterY = distY / 4f;
        float eighthX = distX / 8f;
        float tenthX = distX / 10f;
        float tenthY = distY / 10f;
        float sixteenthX = distX / 16f;
        float sixteenthY = distY / 16f;

        float midX = minX + halfX;

        // V
        obstacle.addLineMirrorH(new Position(Math.round(minX + sixteenthX), Math.round(Math.max(2, minY + tenthY))),
                new Position(Math.round(midX - 0.1f), Math.round(Math.min(minY + 4 * tenthY, halfY - 1))),
                (int) maxX);

        // horizontal middle
        obstacle.addLine(new Position(Math.round(minX + eighthX), Math.round(minY + halfY)),
                new Position(Math.round(maxX - eighthX), Math.round(minY + halfY)));

        // horizontal low left
        obstacle.addLine(new Position(Math.round(minX + tenthX), Math.round(maxY - quarterY)),
                new Position(Math.round(minX + 4 * tenthX), Math.round(maxY - quarterY)));

        // horizontal low right
        obstacle.addLine(new Position(Math.round(maxX - 4 * tenthX), Math.round(maxY - quarterY)),
                new Position(Math.round(maxX - tenthX), Math.round(maxY - quarterY)));

        // vertical low
        obstacle.addLine(new Position(Math.round(midX - 0.1f), Math.round(maxY - 4 * tenthY)),
                new Position(Math.round(midX - 0.1f), Math.round(maxY - sixteenthY)));
        if (distX % 2 == 1) {
            obstacle.addLine(new Position(Math.round(midX + 0.1f), Math.round(maxY - 4 * tenthY)),
                    new Position(Math.round(midX + 0.1f), Math.round(maxY - sixteenthY)));
        }
    }

    public void paintInitialState(Console console) {
        paintBackground(console);
        paint(console);

        console.setTextColor(Console.ANSI_BRIGHT_RED);
        for (Obstacle obstacle : obstacles) {
            obstacle.paint(console);
        }

        paintFood(console);
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

    public void paintFood(Console console) {
        food.paint(console);
    }

    public void paint(Console console) {
        Position head = snake.getHead();

        String s;
        int x = head.getX();
        int y = head.getY();

        if (y % 2 == 1) { // odd line --> upper
            Position lower = new Position(x, y + 1);
            if (snake.contains(lower)) { // is snake below?
                s = "\u2588"; // full block █
            } else if (isObstacleAt(lower)) { // is obstacle below?
                console.setBackgroundColor(Console.ANSI_SALMON_RED_BACKGROUND);
                s = "\u2580"; // upper half ▀
            } else {
                s = "\u2580"; // upper half ▀
            }
        } else { // even line --> lower
            Position upper = new Position(x, y - 1);
            if (snake.contains(upper)) { // is snake above?
                s = "\u2588"; // full block █
            } else if (isObstacleAt(upper)) { // is obstacle above?
                console.setBackgroundColor(Console.ANSI_SALMON_RED_BACKGROUND);
                s = "\u2584"; // lower half ▄
            } else {
                s = "\u2584"; // lower half ▄
            }
        }

        console.setTextColor(Console.ANSI_BRIGHT_YELLOW);
        console.printAt(s, x, (int) Math.round(y / 2.0));
        console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
    }

    public void paintRemove(Console console) {
        if (!snake.isGrowing()) {
            String s = " ";
            Position tail = snake.getTail();
            int x = tail.getX();
            int y = tail.getY();

            if (y % 2 == 1) { // odd line --> upper
                Position lower = new Position(x, y + 1);
                if (snake.contains(lower)) { // is snake below?
                    s = "\u2584"; // lower half ▄
                } else if (isObstacleAt(lower)) { // is obstacle below?
                    console.setTextColor(Console.ANSI_SALMON_RED);
                    s = "\u2584"; // lower half ▄
                }
            } else { // even line --> lower
                Position upper = new Position(x, y - 1);
                if (snake.contains(upper)) { // is snake above?
                    s = "\u2580"; // upper half ▀
                } else if (isObstacleAt(upper)) { // is obstacle above?
                    console.setTextColor(Console.ANSI_SALMON_RED);
                    s = "\u2580"; // upper half ▀
                }
            }

            console.printAt(s, x, (int) Math.round(y / 2.0));
        }
    }

    public GameState checkEvent() {
        GameState result = new GameState();

        if (checkCollision()) {
            result.setGameOver();
        }

        if (hasFoundFood()) {
            result.setFoundFood();
            if (getFood().getLabel() == '9') {
                result.setWin();
            } else {
                addFood();
                int amount = (bottomRight.getX() - topLeft.getX() +
                        bottomRight.getY() - topLeft.getY() + 2) / 6; // 33% of avg
                snake.grow(amount);
            }
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

    public void addFood() {
        Position positionUpper;
        Position positionLower;
        boolean isLoop;
        do {
            int foodX = (int) (topLeft.getX() + Math.random() * bottomRight.getX());
            int foodY = (int) (topLeft.getY() + Math.random() * (bottomRight.getY() - 1));
            if (foodY % 2 == 0) {
                positionUpper = new Position(foodX, foodY - 1);
                positionLower = new Position(foodX, foodY);
            } else {
                positionUpper = new Position(foodX, foodY);
                positionLower = new Position(foodX, foodY + 1);
            }

            isLoop = snake.contains(positionUpper) || snake.contains(positionLower);
            if (!isLoop) {
                for (Obstacle obstacle : obstacles) {
                    if (obstacle.contains(positionUpper) || obstacle.contains(positionLower)) {
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

        food = new Food(positionUpper, positionLower, label);
    }

    public boolean checkCollision() {
        return isOutOfBounds() || snake.isEatingItself() || isObstacleAt(snake.getHead());
    }

    public Food getFood() {
        return food;
    }

    public Snake getSnake() {
        return snake;
    }

    public boolean hasFoundFood() {
        return food.getPosition().contains(snake.getHead());
    }

    private boolean isObstacleAt(Position position) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.contains(position)) {
                return true;
            }
        }

        return false;
    }

    public void moveSnake() {
        snake.move();
    }
}
