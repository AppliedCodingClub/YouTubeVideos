package com.appliedcoding.snakegame.controller;

import com.appliedcoding.io.Console;
import com.appliedcoding.io.KeyboardUtils;
import com.appliedcoding.snakegame.config.Configuration;
import com.appliedcoding.snakegame.model.Direction;
import com.appliedcoding.snakegame.model.Environment;
import com.appliedcoding.snakegame.model.GameState;
import com.appliedcoding.io.Position;
import com.appliedcoding.snakegame.model.Snake;
import com.appliedcoding.snakegame.view.Canvas;
import com.appliedcoding.utils.Utils;

import java.io.IOException;

public class SnakeGame {

    private final GameState state;
    private boolean isRunning;
    private Canvas canvas;
    private Console console;
    private Environment environment;

    public SnakeGame() {
        console = Utils.setupConsole();
        createCanvas();
        state = new GameState();
        environment = new Environment(canvas, state);
    }

    public static void main(String[] args) {
        SnakeGame game = new SnakeGame();
        try {
            game.play();
        } catch (IOException e) {
            game.resetConsole();
            System.err.println(e);
        } finally {
            game.resetConsole();
        }
    }

    public void play() throws IOException {
        Configuration.CANVAS_ENABLED = true;
        canvas.paintInitialState(environment);
        isRunning = true;

        while (isRunning) {
            doLoop();
            Utils.pause(100);
        }
    }

    private void doLoop() throws IOException {
        String keyPressed = KeyboardUtils.readKeyPress();
        handleKeyPress(keyPressed);

        canvas.paintRemove(environment);
        environment.moveSnake();

        if (state.isGameOver() || state.isWin()) {
            isRunning = false;
        }

        canvas.paint(environment);
    }

    private void handleKeyPress(String keyPressed) {
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

    private void createCanvas() {
        Position maxScreen = console.detectScreenSize();
        Position canvasTopLeft = new Position((maxScreen.getX() + 1) / 3, 1);
        canvas = new Canvas(console, canvasTopLeft, maxScreen);
    }

    public void resetConsole() {
        console.gotoXY(1, 1000);
        console.setTextColor(Console.ANSI_RESET);
        console.showCursor();
        console.enterLineMode();
    }
}
