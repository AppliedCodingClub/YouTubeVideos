package com.appliedcoding.snakegame;

import java.io.IOException;

public class SnakeGame {

    private boolean isRunning;
    private String keyPressed;
    private Console console;
    private Environment environment;
    private String nextKey = "";
    private Position maxScreen;

    public SnakeGame() {
        try {
            console = new Console();
            console.enterCharacterMode();
            console.hideCursor();
            console.clear();

            maxScreen = console.detectScreenSize();
            environment = new Environment(new Position(1, 1), new Position(maxScreen.getX(), 2 * maxScreen.getY()));
            environment.paintInitialState(console);

            isRunning = true;
            while (isRunning) {
                doLoop();
                pause(100);
            }

            console.gotoXY(1, maxScreen.getY());
            console.showCursor();
            console.setTextColor(Console.ANSI_RESET);
        } finally {
            console.enterLineMode();
        }
    }

    public static void main(String[] args) {
        new SnakeGame();
    }

    private void doLoop() {
        readKeyPress();
        handleKeyPress();

        environment.paintRemove(console);
        environment.moveSnake();
        checkEvent();
        environment.paint(console);
    }

    private void checkEvent() {
        GameState gameState = environment.checkEvent();

        if (gameState.isGameOver()) {
            isRunning = false;
            console.setBackgroundColor(Console.ANSI_YELLOW_BACKGROUND);
            console.setTextColor(Console.ANSI_RED);
            int x = maxScreen.getX() / 2;
            int y = maxScreen.getY() / 8;
            console.printAt(" GAME OVER ", x - 4, y);
            console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
        } else if (gameState.isWin()) {
            isRunning = false;
            console.setBackgroundColor(Console.ANSI_YELLOW_BACKGROUND);
            console.setTextColor(Console.ANSI_RED);
            int x = maxScreen.getX() / 2;
            int y = maxScreen.getY() / 8;
            console.printAt(" YOU WIN ", x - 3, y);
            console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
        } else if (gameState.hasFoundFood()) {
            environment.paintFood(console);
        }
    }

    private void handleKeyPress() {
        Snake snake = environment.getSnake();
        switch (keyPressed) {
            case "27":
                isRunning = false;
                break;

            case "279165": // up
                snake.setDirection(Direction.Up);
                break;

            case "279166": // down
                snake.setDirection(Direction.Down);
                break;

            case "279167": // right
                snake.setDirection(Direction.Right);
                break;

            case "279168": // left
                snake.setDirection(Direction.Left);
                break;
        }
    }

    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
            isRunning = false;
        }
    }

    private void readKeyPress() {
        try {
            keyPressed = readOneKey();
            if (keyPressed.isEmpty()) { // no key from System.in
                if (!nextKey.isEmpty()) { // we have nextKey from previous time?
                    keyPressed = nextKey;
                    nextKey = "";
                }
            } else { // debounce
                boolean isLoop = true;
                while (isLoop) {
                    String k = readOneKey();
                    if (!k.equals(keyPressed)) { // new key after debounce?
                        nextKey = k;
                        isLoop = false;
                        while (System.in.available() > 0) {
                            System.in.read();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }
    }

    private String readOneKey() {
        String result = "";
        try {
            if (System.in.available() > 0) {
                result += System.in.read();
                if (result.equals("27") && System.in.available() >= 2) { // is arrow key?
                    result += System.in.read();
                    result += System.in.read();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }

        return result;
    }
}