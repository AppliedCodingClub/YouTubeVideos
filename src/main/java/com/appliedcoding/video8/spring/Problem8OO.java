package com.appliedcoding.video8.spring;

import com.appliedcoding.io.CanvasBase;
import com.appliedcoding.io.Console;
import com.appliedcoding.io.KeyboardUtils;
import com.appliedcoding.io.Position;
import com.appliedcoding.utils.Utils;

import java.io.IOException;

public class Problem8OO {

    public static final int WIDTH = 110;
    public static final int HEIGHT = 100;
    public static final int PAUSE = 100;

    private CanvasBase canvas;

    private Ball anchor;
    private Ball bob;
    private Spring spring;

    private float time;
    private float dt = 0.5f;

    public Problem8OO() {
        Console console = new Console();
        canvas = new CanvasBase(console, new Position(1, 1), WIDTH, HEIGHT);
    }

    public static void main(String[] args) {
        Problem8OO program = null;

        try {
            program = new Problem8OO();
            program.setup();
            program.run();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if (program != null) {
                program.cleanup();
            }
        }
    }

    private void setup() {
        Console console = canvas.getConsole();
        console.enterCharacterMode();
        console.hideCursor();
        console.clear();

        anchor = new Ball(55, 5, 5, "A", 10);
        bob = new Ball(55 + 20, 5, 10, "B", 204);
        spring = new Spring(5, 25, anchor, bob);

        anchor.setLocked(true);
    }

    private void run() throws IOException {
        boolean isRunning = true;

        while (isRunning) {
            String key = KeyboardUtils.readKeyPress();
            if (key.equals(KeyboardUtils.ESC_KEY)) {
                isRunning = false;
            }

            update();
            show();
            printStats();

            Utils.pause(PAUSE);

            time += dt;
        }
    }

    private void update() {
        spring.update(time);
    }

    private void show() {
        canvas.setBackgroundColor(15);
        canvas.clear();

        spring.show(canvas);

        //line
//        canvas.setColor(45);
//        canvas.line(anchor.getX(), anchor.getY(), bob.getX(), bob.getY());

        //anchor
//        canvas.setColor(10);
//        canvas.setFillColor(10);
//        canvas.midptellipse(anchor.getX(), anchor.getY(), 2, 2);

        //bob
//        canvas.setColor(204);
//        canvas.setFillColor(204);
//        canvas.midptellipse(bob.getX(), bob.getY(), 2, 2);
    }

    private void printStats() {
        canvas.setColor(23);
        canvas.setBackgroundColor(15);
        canvas.printAt(String.format("dist:%6.2f", anchor.getPosition().subtract(bob.getPosition()).getMagnitude()),
                2, 2);
    }

    private void cleanup() {
        Console console = canvas.getConsole();
        console.gotoXY(1, HEIGHT / 2 + 2);
        console.setTextColor(Console.ANSI_RESET);
        console.enterLineMode();
        console.showCursor();
    }
}
