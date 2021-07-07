package com.appliedcoding.video8.spring;

import com.appliedcoding.io.CanvasBase;
import com.appliedcoding.io.Console;
import com.appliedcoding.io.KeyboardUtils;
import com.appliedcoding.io.Position;
import com.appliedcoding.utils.Utils;

import java.io.IOException;

public class Problem8Vector {

    public static final int WIDTH = 110;
    public static final int HEIGHT = 100;
    public static final int PAUSE = 100;
    public static final float ATTENUATION = 0.95f;

    private final Vector GRAVITY = new Vector(0, 9.81f);

    private Vector acceleration;
    private Vector velocity;
    private CanvasBase canvas;

    private float restLength = 25f;
    private float k = 5f;
    private float mass = 10f; // kg

    private Vector anchor = new Vector(40, 20);
    private Vector bob = new Vector(75, 30);

    public Problem8Vector() {
        Console console = new Console();
        canvas = new CanvasBase(console, new Position(1, 1), WIDTH, HEIGHT);
    }

    public static void main(String[] args) {
        Problem8Vector program = null;

        try {
            program = new Problem8Vector();
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

            update();
            show();
            printStats();

            Utils.pause(PAUSE);
        }
    }

    // F = m * a
    // F = -k * x  ==>  a = -k * x / m
    private void update() {

        Vector bToA = bob.subtract(anchor);
        float magnitude = bToA.getMagnitude();
        Vector x = bToA.normalize().multiply(magnitude - restLength);

        acceleration = x.multiply(-k).divide(mass);
        acceleration = acceleration.add(GRAVITY);

        // v += a * t
        velocity = velocity.add(acceleration).multiply(ATTENUATION);

        // d += v * t
        bob = bob.add(velocity);
//        anchor = anchor.subtract(velocity);
    }

    private void show() {
        canvas.setBackgroundColor(15);
        canvas.clear();

        //line
        canvas.setColor(45);
//        canvas.line(10, -50, 20, 150);
        canvas.line(anchor.getSizeX(), anchor.getSizeY(), bob.getSizeX(), bob.getSizeY());

        //anchor
        canvas.setColor(10);
        canvas.setFillColor(10);
        canvas.midptellipse(anchor.getSizeX(), anchor.getSizeY(), 2, 2);

        //bob
        canvas.setColor(204);
        canvas.setFillColor(204);
        canvas.midptellipse(bob.getSizeX(), bob.getSizeY(), 2, 2);
    }

    private void printStats() {
        canvas.setColor(23);
        canvas.setBackgroundColor(15);
        canvas.printAt(String.format("dst:%f", anchor.subtract(bob).getMagnitude()), 2, 2);
        canvas.printAt(String.format("acc:%s", acceleration), 2, 4);
        canvas.printAt(String.format("vel:%s", velocity), 2, 6);
    }

    private void setup() {
        Console console = canvas.getConsole();
        console.enterCharacterMode();
        console.hideCursor();
        console.clear();

        acceleration = Vector.NULL;
        velocity = Vector.NULL;
    }

    private void cleanup() {
        Console console = canvas.getConsole();
        console.gotoXY(1, HEIGHT / 2 + 2);
        console.setTextColor(Console.ANSI_RESET);
        console.enterLineMode();
        console.showCursor();
    }
}
