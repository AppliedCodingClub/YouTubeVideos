package com.appliedcoding.video8.spring;

import com.appliedcoding.io.CanvasBase;
import com.appliedcoding.io.Console;
import com.appliedcoding.io.KeyboardUtils;
import com.appliedcoding.io.Position;
import com.appliedcoding.utils.Utils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputAdapter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Problem8Chain extends NativeMouseInputAdapter {

    public static final int WIDTH = 110;
    public static final int HEIGHT = 100;
    public static final int FRAME_PAUSE = 100; // ms
    public static final int FRAME_SKIP = 80;
    public static final int CHAIN_LENGTH = 50;

    private static final float k = 800f; // N/m
    private static final float restLength = 1f; // m
    private static final float spacing = 1f; // m
    private static final float mass = 0.5f; // kg
    private final float dt = 0.01f; // seconds
    private float time;
    private int frame;
    private CanvasBase canvas;
    private Ball[] balls = new Ball[CHAIN_LENGTH + 1];
    private Spring[] springs = new Spring[CHAIN_LENGTH];

    private Vector ballInitialPosition;
    private Vector mousePress;
    private int ballIndex;

    public Problem8Chain() {
        Console console = new Console();
        canvas = new CanvasBase(console, new Position(1, 1), WIDTH, HEIGHT);
    }

    public static void main(String[] args) {
        Problem8Chain program = null;

        try {
            program = new Problem8Chain();
            program.setup();
            program.run();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if (program != null) {
                program.cleanup();
            }
        }
    }

    private void setup() {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseListener(this);
            GlobalScreen.addNativeMouseMotionListener(this);
        } catch (NativeHookException ex) {
            System.err.println(ex);
            System.exit(1);
        }

        Console console = canvas.getConsole();
        console.enterCharacterMode();
        console.hideCursor();
        console.clear();

        for (int i = 0; i < balls.length; i++) {
            int color = -1;
            if (i == 0) {
                color = 10;
            } else if (i == balls.length - 1) {
                color = 204;
            }

            balls[i] = new Ball(WIDTH / 3 + spacing * i, HEIGHT / 4, mass, "" + i, color);
        }

        balls[0].setLocked(true);

        for (int i = 0; i < springs.length; i++) {
            springs[i] = new Spring(k, restLength, balls[i], balls[i + 1]);
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

            if (frame % FRAME_SKIP == 0) {
                canvas.setBackgroundColor(15);
                canvas.clear();

//                printStats();
                show();
                Utils.pause(FRAME_PAUSE);
            }

            time += dt;
            frame++;
        }
    }

    private void update() {
        for (Spring spring : springs) {
            spring.update(time);
        }
    }

    private void show() {
        for (Spring spring : springs) {
            spring.show(canvas);
        }
    }

    private void printStats() {
        canvas.getConsole().setTextColor(Console.ANSI_RESET);
        canvas.setColor(23);

        int y = HEIGHT + 2;

        canvas.printAt(String.format("time:%.2f  ", time), 2, y);

        for (int i = 0; i < balls.length; i++) {
            if (i == balls.length - 1) {
                canvas.printAt(String.format("%s", balls[i]), 2, y + 2 + 2 * i);
            } else {
                float magnitude = balls[i].getPosition().subtract(balls[i + 1].getPosition()).getMagnitude();
                canvas.printAt(String.format("%s dist:%6.2f  ", balls[i], magnitude), 2, y + 2 + 2 * i);
            }
        }
    }

    private void cleanup() {
        Console console = canvas.getConsole();
        console.gotoXY(1, 1000);
        console.setTextColor(Console.ANSI_RESET);
        console.enterLineMode();
        console.showCursor();

        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        mousePress = new Vector(e.getX(), e.getY());
        ballIndex = selectBall(mousePress);
        ballInitialPosition = balls[ballIndex].getPosition();
        balls[ballIndex].setLocked(true);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        if (ballIndex > 0) {
            balls[ballIndex].setLocked(false);
        }
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        Vector mouseDrag = new Vector(e.getX(), e.getY()).subtract(mousePress);
        balls[ballIndex].setPosition(ballInitialPosition.add(mouseDrag.divide(10)));
    }

    private int selectBall(Vector mousePress) {
        Vector normalized = mousePress.divide(10);
        float distFirst = balls[0].getPosition().subtract(normalized).getMagnitude();
        float distLast = balls[balls.length - 1].getPosition().subtract(normalized).getMagnitude();

        return distFirst < distLast ? 0 : (balls.length - 1);
    }
}
