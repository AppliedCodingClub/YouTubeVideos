package com.appliedcoding.video2;

import com.appliedcoding.video1.Console;

import java.io.IOException;

public class SnakeGame {

    static boolean isRunning = true;
    static String keyPressed;
    static Console console = new Console();
    static Environment environment;
    static Snake snake;
    static int foodCount = 1;

    static {
        snake = new Snake(new Position(40, 12));
        snake.grow(20);


        environment = new Environment(new Position(0, 0), new Position(79, 23));
        Obstacle obstacle = new Obstacle();
        obstacle.addLine(new Position(5, 8), new Position(5, 16));
        obstacle.addLine(new Position(74, 8), new Position(74, 16));
        obstacle.addLine(new Position(15, 5), new Position(64, 5));
        obstacle.addLine(new Position(15, 19), new Position(64, 19));
        environment.addObstacle(obstacle);
        environment.setSnake(snake);
        environment.addFood(("" + foodCount).charAt(0));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] cmd;
        try {
            cmd = new String[]{"/bin/sh", "-c", "stty raw </dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();

            environment.paint(console);

            while (isRunning) {
                doLoop();
                pause(100);
            }
        } finally {
            cmd = new String[]{"/bin/sh", "-c", "stty sane </dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();
        }
    }

    private static void doLoop() throws IOException {
        readPressedKey();
        handlePressedKey();

        paintRemove();
        calculateNextState();
        paint();
        collisionDetection();

        console.printScreen();
    }

    private static void calculateNextState() {
        snake.move();
    }

    private static void paint() {
        Position head = snake.getHead();
        console.putCharAt('*', head.getY(), head.getX());
        Position neck = snake.getNeck();
        console.putCharAt('█', neck.getY(), neck.getX());
    }

    private static void paintRemove() {
        Position tail = snake.getTail();
        console.putCharAt(' ', tail.getY(), tail.getX());
    }

    private static void collisionDetection() {
        Position head = snake.getHead();
        if (environment.isOutOfBounds(head) || environment.isCollision(head) || snake.isEatingItself()) {
            isRunning = false;
            console.putStringAt("GAME OVER", 1, 1);
        } else if (environment.hasSnakeFoundFood()) {
            snake.grow(10);
            foodCount++;
            if (foodCount == 10) {
                isRunning = false;
                console.putStringAt("YOU WIN", 1, 1);
            } else {
                environment.addFood(("" + foodCount).charAt(0));
                environment.paint(console);
            }
        }
    }

    private static void readPressedKey() throws IOException {
        keyPressed = "";
        while (System.in.available() > 0) {
            keyPressed += System.in.read();
        }
    }

    private static void handlePressedKey() {
        switch (keyPressed) {
            case "27": // ESC
                isRunning = false;
                break;

            case "279168": // left
                snake.setDirection(Direction.Left);
                break;

            case "279165": // up
                snake.setDirection(Direction.Up);
                break;

            case "279167": // right
                snake.setDirection(Direction.Right);
                break;

            case "279166": // down
                snake.setDirection(Direction.Down);
                break;
        }
    }

    private static void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore for now
        }
    }
}
