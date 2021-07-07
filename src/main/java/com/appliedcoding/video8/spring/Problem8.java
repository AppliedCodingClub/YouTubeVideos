package com.appliedcoding.video8.spring;

import com.appliedcoding.io.CanvasBase;
import com.appliedcoding.io.Console;
import com.appliedcoding.io.KeyboardUtils;
import com.appliedcoding.io.Position;
import com.appliedcoding.utils.Utils;

import java.io.IOException;

public class Problem8 {

    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;
    public static final float ATTENUATION = 0.95f;

    private float restLength = 60;
    private float length = 95;
    private float k = 1f;

    private float acceleration;
    private float velocity;
    private CanvasBase canvas;

    public Problem8() {
        Console console = new Console();
        canvas = new CanvasBase(console, new Position(1, 1), WIDTH, HEIGHT);
    }

    public static void main(String[] args) {
        Problem8 program = null;

        try {
            program = new Problem8();
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

    private void run() throws IOException {
        boolean isRunning = true;

        while (isRunning) {
            String key = KeyboardUtils.readKeyPress();
            if (key.equals(KeyboardUtils.ESC_KEY)) {
                isRunning = false;
            }

            canvas.setBackgroundColor(15);
            canvas.clear();

            update();
            show();
            printStats();

            Utils.pause(50);
        }
    }

    private void update() {
        // F = m * a
        // d += v * t
        // v += a * t
        // F = -k * x

        float x = length - restLength;
        acceleration = -k * x;
        velocity += acceleration;
        velocity *= ATTENUATION;
        if (Math.abs(velocity) < 0.5f) {
            velocity = 0;
        }

        length += velocity;
    }

    private void show() {
        canvas.setColor(13);
        canvas.line(WIDTH / 2, 0, WIDTH / 2, length - 3);

        canvas.setColor(9);
        canvas.setFillColor(9);
        canvas.midptellipse(WIDTH / 2, length, 2.1f, 2f);
    }

    private void printStats() {
        canvas.printAt(String.format("x  :%6.2f", length - restLength), 2, 2);
        canvas.printAt(String.format("acc:%6.2f", acceleration), 2, 4);
        canvas.printAt(String.format("vel:%6.2f", velocity), 2, 6);
    }

    private void setup() {
        Console console = canvas.getConsole();
        console.enterCharacterMode();
        console.hideCursor();
        console.clear();
    }

    private void cleanup() {
        Console console = canvas.getConsole();
        console.gotoXY(1, HEIGHT / 2 + 2);
        console.setTextColor(Console.ANSI_RESET);
        console.enterLineMode();
        console.showCursor();
    }
}
