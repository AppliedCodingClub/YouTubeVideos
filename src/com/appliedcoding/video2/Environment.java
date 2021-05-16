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

        double minX = topLeft.getX();
        double maxX = bottomRight.getX();
        double minY = topLeft.getY();
        double maxY = bottomRight.getY();
        double sizeX = maxX - minX + 1;
        double sizeY = maxY - minY + 1;
        double halfX = sizeX / 2.0;
        double halfY = sizeY / 2.0;
        double quarterY = sizeY / 4.0;
        double eighthX = sizeX / 8.0;
        double tenthX = sizeX / 10.0;
        double tenthY = sizeY / 10.0;
        double sixteenthX = sizeX / 16.0;
        double sixteenthY = sizeY / 16.0;

        obstacle.addLine(new Position((int) sixteenthX, (int) Math.max(2, tenthY)),
                new Position((int) halfX, (int) Math.min(4 * tenthY, halfY - 3)));
        obstacle.addLine(new Position((int) halfX, (int) Math.min(4 * tenthY, halfY - 3)),
                new Position((int) (maxX - sixteenthX), (int) Math.max(2, tenthY)));

        obstacle.addLine(new Position((int) eighthX, (int) (halfY - 1)),
                new Position((int) (maxX - eighthX), (int) (halfY - 1)));

        obstacle.addLine(new Position((int) tenthX, (int) (maxY - quarterY)),
                new Position((int) (4 * tenthX), (int) (maxY - quarterY)));
        obstacle.addLine(new Position((int) (maxX - 4 * tenthX), (int) (maxY - quarterY)),
                new Position((int) (maxX - tenthX), (int) (maxY - quarterY)));

        obstacle.addLine(new Position((int) halfX, (int) (maxY - 4 * tenthY)),
                new Position((int) halfX, (int) (maxY - sixteenthY)));
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
        List<Position> body = snake.getBody();

        String s;
        int x = head.getX();
        int y = head.getY();

        if (y % 2 == 1) { // odd line --> upper
            Position lower = new Position(x, y + 1);
            if (body.contains(lower)) { // is snake below?
                s = "\u2588"; // full block █
            } else if (isObstacleAt(lower)) { // is obstacle below?
                console.setBackgroundColor(Console.ANSI_SALMON_RED_BACKGROUND);
                s = "\u2580"; // upper half ▀
            } else {
                s = "\u2580"; // upper half ▀
            }
        } else { // even line --> lower
            Position upper = new Position(x, y - 1);
            if (body.contains(upper)) { // is snake above?
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
            List<Position> body = snake.getBody();
            int x = tail.getX();
            int y = tail.getY();

            if (y % 2 == 1) { // odd line --> upper
                Position lower = new Position(x, y + 1);
                if (body.contains(lower)) { // is snake below?
                    s = "\u2584"; // lower half ▄
                } else if (isObstacleAt(lower)) { // is obstacle below?
                    console.setTextColor(Console.ANSI_SALMON_RED);
                    s = "\u2584"; // lower half ▄
                }
            } else { // even line --> lower
                Position upper = new Position(x, y - 1);
                if (body.contains(upper)) { // is snake above?
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

    public boolean checkCollision() {
        return isOutOfBounds() || snake.isEatingItself() || isObstacleAt(snake.getHead());
    }

    private boolean isObstacleAt(Position position) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getBody().contains(position)) {
                return true;
            }
        }

        return false;
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

            isLoop = snake.getBody().contains(positionUpper) || snake.getBody().contains(positionLower);
            if (!isLoop) {
                for (Obstacle obstacle : obstacles) {
                    if (obstacle.getBody().contains(positionUpper) ||
                            obstacle.getBody().contains(positionLower)) {
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

    public Food getFood() {
        return food;
    }

    public boolean hasFoundFood() {
        return food.getPosition().contains(snake.getHead());
    }

    public void moveSnake() {
        snake.move();
    }

    public Snake getSnake() {
        return snake;
    }
}
