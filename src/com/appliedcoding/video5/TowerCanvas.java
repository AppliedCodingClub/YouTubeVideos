package com.appliedcoding.video5;

import com.appliedcoding.io.CanvasBase;
import com.appliedcoding.io.Console;
import com.appliedcoding.io.KeyboardUtils;
import com.appliedcoding.snakegame.model.Position;
import com.appliedcoding.utils.Utils;

import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;

public class TowerCanvas extends CanvasBase {

    public final int PALETTE_DISK = 96;
    public final int PALETTE_PEG = 214;
    public final int PALETTE_MARKER = 203;
    public final int PALETTE_TEXT = 24;

//    public final int PALETTE_DISK = 24;
//    public final int PALETTE_PEG = 214;
//    public final int PALETTE_MARKER = 203;
//    public final int PALETTE_TEXT = 96;

    public final int INIT_X;
    public final int INIT_Y;
    public final int MAX_TOWER_WIDTH = 26; // must be even
    private final Console console;
    private int frame;
    private int disks;
    private int moveCount;
    private int markerA;
    private int markerB;

    public TowerCanvas(Console console, Position topleft, int width, int height, Deque<Disk> tower) {
        super(console, topleft, width, height);
        this.console = console;
        disks = tower.size();

        INIT_Y = getHeight() - 5;
        INIT_X = getWidth() / 2 - MAX_TOWER_WIDTH;

        console.hideCursor();

        int y = INIT_Y;
        for (Iterator<Disk> it = tower.descendingIterator(); it.hasNext(); ) {
            Disk disk = it.next();
            disk.setX(INIT_X); // middle point (i.e. peg)
            disk.setY(y);
            y -= disk.getHeight();
        }
    }

    public void printMove(Deque<Disk>[] towers, int fromTower, int toTower) {
        moveCount++;
        markerA = INIT_X + MAX_TOWER_WIDTH * fromTower;
        markerB = INIT_X + MAX_TOWER_WIDTH * toTower;
        Disk disk = towers[fromTower].peek();
        int y = disk.getY();
        int destinationY = INIT_Y - disk.getHeight() * (disks + 1) - 2;
        do { //move up
            y--;
            disk.setY(y);
            frame++;
            drawState(towers, Configuration.FRAME_RATE);
        } while (y > destinationY);

        if (frame % Configuration.FRAME_RATE != 0) {
            printState(towers);
        }

        int destinationX = markerB;
        int x = disk.getX();
        int dx = destinationX > x ? 2 : -2;
        do { //move sideways
            x += dx;
            disk.setX(x);
            frame++;
            drawState(towers, Configuration.FRAME_RATE);
        } while (Math.abs(x - destinationX) > 0);

        if (frame % Configuration.FRAME_RATE != 0) {
            printState(towers);
        }

        Disk desinationTop = towers[toTower].peek();
        destinationY = desinationTop == null ? INIT_Y : (desinationTop.getY() - disk.getHeight());
        do { //move down
            y++;
            disk.setY(y);
            frame++;
            drawState(towers, Configuration.FRAME_RATE);
        } while (y < destinationY);

        if (frame % Configuration.FRAME_RATE != 0) {
            printState(towers);
        }
    }

    public void printState(Deque<Disk>[] towers) {
        drawState(towers, 1);
    }

    private void drawState(Deque<Disk>[] towers, int frameRate) {
        if (frame % frameRate != 0) {
            return;
        }

        clear();
        drawMarkers(2);
        drawPegs(towers);
        drawDisks(towers);
        drawStats(2, 0);

        handleKeyPress();

        if (Configuration.FRAME_PAUSE > 0) {
            Utils.pause(Configuration.FRAME_PAUSE);
        }
    }

    private void drawPegs(Deque<Disk>[] towers) {
        for (int i = 0; i < towers.length; i++) {
            drawPeg(INIT_X + MAX_TOWER_WIDTH * i, INIT_Y - disks - 1);
        }
    }

    private void drawPeg(int x, int y) {
        setColor(PALETTE_PEG);
        rectangle(x, y, 1, disks + 2);
    }

    private void drawDisks(Deque<Disk>[] towers) {
        for (int i = 0; i < towers.length; i++) {
            for (Iterator<Disk> it = towers[i].descendingIterator(); it.hasNext(); ) {
                Disk disk = it.next();
                drawDisk(disk);
            }
        }
    }

    private void drawDisk(Disk disk) {
        int x = disk.getX();
        int y = disk.getY();
        int diskWidth = disk.getWidth();
        setColor(PALETTE_DISK);
        rectangle(x - diskWidth / 2, y, diskWidth, disk.getHeight());
    }

    private void drawMarkers(int y) {
        setColor(PALETTE_MARKER);
        if (markerA > markerB) {
            plot(markerB + 1, y + 1);
            plot(markerB + 2, y + 1);
            plot(markerB + 2, y);
        } else {
            plot(markerB - 1, y + 1);
            plot(markerB - 2, y + 1);
            plot(markerB - 2, y);
        }

        line(markerA, y + 2, markerB, y + 2);
    }

    private void drawStats(int x, int y) {
        setColor(PALETTE_TEXT);
        printAt(String.format("Moves:%d | Pause:%d | FrameRate:%-5d",
                moveCount, Configuration.FRAME_PAUSE, Configuration.FRAME_RATE), x, y);
    }

    public void cleanup() {
        console.gotoXY(1, getHeight() / 2 + 3);
        console.setTextColor(Console.ANSI_RESET);
        console.showCursor();
    }

    private void handleKeyPress() {
        try {
            String key = KeyboardUtils.readKeyPress();
            switch (key) {
                case KeyboardUtils.ESC_KEY:
                    Configuration.IS_RUNNING = false;
                    break;

                case KeyboardUtils.PLUS_KEY:
                    if (Configuration.FRAME_RATE == 1) {
                        Configuration.FRAME_RATE = 2;
                    } else {
                        Configuration.FRAME_RATE += 2;
                    }
                    break;

                case KeyboardUtils.UNDERSCORE_KEY:
                    Configuration.FRAME_RATE = Math.max(1, Configuration.FRAME_RATE - 2);
                    break;

                case KeyboardUtils.EQUALS_KEY:
                    Configuration.FRAME_PAUSE = Math.max(0, Configuration.FRAME_PAUSE - 10);
                    break;

                case KeyboardUtils.MINUS_KEY:
                    Configuration.FRAME_PAUSE += 10;
                    break;
            }
        } catch (IOException e) {
            System.err.println(e);
            Configuration.IS_RUNNING = false;
        }
    }
}
