package com.appliedcoding.video2;

import java.io.IOException;

public class SnakeGame {

    private boolean isRunning;
    private String keyPressed;
    private Console console;
    private Environment environment;
    private String nextKey = "";

    public SnakeGame() {
        try {
            console = new Console();
            console.enterCharacterMode();
            console.hideCursor();
            console.clear();

            Position maxScreen = console.detectScreenSize();
            environment = new Environment(new Position(1, 1), new Position(maxScreen.getX(), maxScreen.getY()));
            paintInitialState();

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
        environment.calculateNextState();
        checkEvent();
        environment.paint(console);
    }

    private void paintInitialState() {
        environment.paintBackground(console);
        environment.paint(console);
    }

    private void checkEvent() {
        GameState gameState = environment.checkEvent();

        if (gameState.isGameOver()) {
            isRunning = false;
            console.setBackgroundColor(Console.ANSI_YELLOW_BACKGROUND);
            console.setTextColor(Console.ANSI_RED);
            console.printAt(" GAME OVER ", 2, 2);
            console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
        } else if (gameState.isWin()) {
            isRunning = false;
            console.setBackgroundColor(Console.ANSI_YELLOW_BACKGROUND);
            console.setTextColor(Console.ANSI_RED);
            console.printAt(" YOU WIN ", 2, 2);
            console.setBackgroundColor(Console.ANSI_BLUE_BACKGROUND);
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
            if (keyPressed.isEmpty()) {
                if (!nextKey.isEmpty()) {
                    keyPressed = nextKey;
                    nextKey = "";
                }
            } else { // debounce
                boolean isLoop = true;
                while (isLoop) {
                    String k = readOneKey();
                    if (!k.equals(keyPressed)) {
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
