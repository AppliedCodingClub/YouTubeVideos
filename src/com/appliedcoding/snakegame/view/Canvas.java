package com.appliedcoding.snakegame.view;

import com.appliedcoding.io.Console;
import com.appliedcoding.snakegame.config.Configuration;
import com.appliedcoding.snakegame.model.Environment;
import com.appliedcoding.snakegame.model.Food;
import com.appliedcoding.snakegame.model.GameState;
import com.appliedcoding.snakegame.model.Obstacle;
import com.appliedcoding.snakegame.model.Position;
import com.appliedcoding.snakegame.model.Snake;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Canvas {

    private final Console console;
    private final Position topLeft;
    private Position bottomRight;
    private int width;
    private int height;

    // real screen coordinates
    public Canvas(Console console, Position topLeft, Position bottomRight) {
        this.console = console;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        width = bottomRight.getX() - topLeft.getX() + 1;
        height = bottomRight.getY() - topLeft.getY() + 1;
    }

    public void resize(int amount) {
        bottomRight = new Position(bottomRight.getX() + 2 * amount, bottomRight.getY() + amount);
        width = bottomRight.getX() - topLeft.getX() + 1;
        height = bottomRight.getY() - topLeft.getY() + 1;
    }

    public void paintInitialState(Environment environment) {
        if (!Configuration.CANVAS_ENABLED) {
            return;
        }

        paintBackground();
        paintSnake(environment);
        paintObstacle(environment.getObstacle());
        paintFood(environment.getFood());
    }

    public void paint(Environment environment) {
        if (!Configuration.CANVAS_ENABLED) {
            return;
        }

        GameState gameState = environment.getGameState();

        if (gameState.getFoodCredits() == 0) {
            printMessage(" HUNGER GAMES ");
        } else if (gameState.isGameOver()) {
            printMessage(" GAME OVER ");
        } else if (gameState.isWin()) {
            printMessage(" YOU WIN ");
        } else if (gameState.hasFoundFood()) {
            paintFood(environment.getFood());
            paintSnake(environment);
        } else {
            paintSnake(environment);
        }
    }

    public void paintBackground() {
        if (!Configuration.CANVAS_ENABLED) {
            return;
        }

        console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
        char[] chars = new char[width];
        Arrays.fill(chars, ' ');
        String line = new String(chars);
        for (int y = topLeft.getY(), limit = bottomRight.getY(); y <= limit; y++) {
            console.printAt(line, topLeft.getX(), y);
        }
    }

    public void paintObstacle(Obstacle obstacle) {
        if (!Configuration.CANVAS_ENABLED) {
            return;
        }

        console.setTextColor(Console.ANSI_SALMON_RED);
        Set<Position> body = obstacle.getBody();

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

            console.printAt(s, toConsolePosition(x, y));
        }
    }

    public void paintFood(Food food) {
        if (!Configuration.CANVAS_ENABLED) {
            return;
        }

        console.setTextColor(Console.ANSI_GREEN);
        console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
        List<Position> position = food.getPosition();
        console.printAt(String.valueOf(food.getLabel()),
                toConsolePosition(position.get(0).getX(), position.get(1).getY()));
    }

    public void paintSnake(Environment environment) {
        if (!Configuration.CANVAS_ENABLED) {
            return;
        }

        Snake snake = environment.getSnake();
        Position head = snake.getHead();
        console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);

        String s;
        int x = head.getX();
        int y = head.getY();

        if (y % 2 == 1) { // odd line --> upper
            Position lower = new Position(x, y + 1);
            if (snake.contains(lower)) { // is snake below?
                s = "\u2588"; // full block █
            } else if (environment.isObstacleAt(lower)) { // is obstacle below?
                console.setBackgroundColor(Console.ANSI_SALMON_RED_BACKGROUND);
                s = "\u2580"; // upper half ▀
            } else {
                s = "\u2580"; // upper half ▀
            }
        } else { // even line --> lower
            Position upper = new Position(x, y - 1);
            if (snake.contains(upper)) { // is snake above?
                s = "\u2588"; // full block █
            } else if (environment.isObstacleAt(upper)) { // is obstacle above?
                console.setBackgroundColor(Console.ANSI_SALMON_RED_BACKGROUND);
                s = "\u2584"; // lower half ▄
            } else {
                s = "\u2584"; // lower half ▄
            }
        }

        console.setTextColor(Console.ANSI_BRIGHT_YELLOW);
        console.printAt(s, toConsolePosition(x, y));
        console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
    }

    public void paintRemove(Environment environment) {
        if (!Configuration.CANVAS_ENABLED) {
            return;
        }

        Snake snake = environment.getSnake();

        if (!snake.isGrowing()) {
            console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
            String s = " ";
            Position tail = snake.getTail();
            int x = tail.getX();
            int y = tail.getY();

            if (y % 2 == 1) { // odd line --> upper
                Position lower = new Position(x, y + 1);
                if (snake.contains(lower)) { // is snake below?
                    console.setTextColor(Console.ANSI_BRIGHT_YELLOW);
                    s = "\u2584"; // lower half ▄
                } else if (environment.isObstacleAt(lower)) { // is obstacle below?
                    console.setTextColor(Console.ANSI_SALMON_RED);
                    s = "\u2584"; // lower half ▄
                }
            } else { // even line --> lower
                Position upper = new Position(x, y - 1);
                if (snake.contains(upper)) { // is snake above?
                    console.setTextColor(Console.ANSI_BRIGHT_YELLOW);
                    s = "\u2580"; // upper half ▀
                } else if (environment.isObstacleAt(upper)) { // is obstacle above?
                    console.setTextColor(Console.ANSI_SALMON_RED);
                    s = "\u2580"; // upper half ▀
                }
            }

            console.printAt(s, toConsolePosition(x, y));
        }
    }

    private void printMessage(String message) {
        if (!Configuration.CANVAS_ENABLED) {
            return;
        }

        console.setBackgroundColor(Console.ANSI_YELLOW_BACKGROUND);
        console.setTextColor(Console.ANSI_RED);
        int x = topLeft.getX() + (bottomRight.getX() - topLeft.getX()) / 2;
        int y = Math.max(2, topLeft.getY() + (bottomRight.getY() - topLeft.getY()) / 8);
        console.printAt(message, x - message.length() / 2, y);
    }

    private Position toConsolePosition(int x, int y) {
        return new Position(x + topLeft.getX() - 1, (int) Math.round(y / 2.0) + topLeft.getY() - 1);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
