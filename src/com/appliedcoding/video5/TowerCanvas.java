package com.appliedcoding.video5;

import com.appliedcoding.io.CanvasBase;
import com.appliedcoding.io.Console;
import com.appliedcoding.snakegame.model.Position;
import com.appliedcoding.utils.Utils;

import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;

public class TowerCanvas extends CanvasBase {

    public static final int MILLIS = 100;
    public static final int INIT_X = 22;
    public static final int INIT_Y = 60;
    public static final int MAX_TOWER_WIDTH = 26; // must be even
    public static final int FRAME_RATE = 3;

    private final Console console;
    private int frame;

    public TowerCanvas(Console console, Position topleft, int width, int height, Deque<Disk> tower) {
        super(console, topleft, width, height);
        this.console = console;
        console.hideCursor();

        int y = INIT_Y;
        for (Iterator<Disk> it = tower.descendingIterator(); it.hasNext(); ) {
            Disk disk = it.next();
            disk.setX(INIT_X); // middle point (i.e. peg)
            disk.setY(y);
            y -= disk.getHeight();
        }
    }

    public void printMove(Deque<Disk>[] towers, int fromTower, int destinationTower) {
        Disk disk = towers[fromTower].peek();
        int y = disk.getY();
        int destinationY = INIT_Y - disk.getHeight() * (HanoiTowersRecursive.DISKS + 1);
        do { //move up
            y--;
            disk.setY(y);
            frame++;
            drawState(towers, FRAME_RATE);
        } while (y > destinationY);

        if (frame % FRAME_RATE != 0) {
            printState(towers);
        }

        int destinationX = INIT_X + MAX_TOWER_WIDTH * destinationTower;
        int x = disk.getX();
        int dx = destinationX > x ? 2 : -2;
        do { //move sideways
            x += dx;
            disk.setX(x);
            frame++;
            drawState(towers, FRAME_RATE);
        } while (Math.abs(x - destinationX) > 0);

        if (frame % FRAME_RATE != 0) {
            printState(towers);
        }

        Disk desinationTop = towers[destinationTower].peek();
        destinationY = desinationTop == null ? INIT_Y : (desinationTop.getY() - disk.getHeight());
        do { //move down
            y++;
            disk.setY(y);
            frame++;
            drawState(towers, FRAME_RATE);
        } while (y < destinationY);

        if (frame % FRAME_RATE != 0) {
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

        console.clear();

        for (int i = 0; i < towers.length; i++) {
            drawPeg(INIT_X + MAX_TOWER_WIDTH * i, INIT_Y);
        }

        for (int i = 0; i < towers.length; i++) {
            for (Iterator<Disk> it = towers[i].descendingIterator(); it.hasNext(); ) {
                Disk disk = it.next();
                drawDisk(disk);
            }
        }

        console.setTextColor(Console.ANSI_RESET);
//        console.printAt("MaxCallStack: " + hanoiTowers.maxCallStack, 1, 62);
//        console.printAt("MoveCount: " + hanoiTowers.moveCount, 1, 63);

        if (MILLIS > 0) {
            Utils.pause(MILLIS);
        }
    }

    private void drawDisk(Disk disk) {
        int x = disk.getX();
        int y = disk.getY();
        int diskWidth = disk.getWidth();
        console.setTextColor(Console.ANSI_BRIGHT_RED);
        drawRectangle(x - diskWidth / 2, y, diskWidth, disk.getHeight());
    }

    private void drawPeg(int x, int y) {
        console.setTextColor(Console.ANSI_GREEN);
        drawRectangle(x, y, 1, 1 * (HanoiTowersRecursive.DISKS + 1));
    }

    private void drawRectangle(int x, int y, int width, int height) {
        char[] chars = new char[width];
        Arrays.fill(chars, '\u2588');
        String row = new String(chars);
        for (int i = 0; i < height; i++) {
            console.printAt(row, x, y - i);
        }
    }

    public void cleanup() {
        console.gotoXY(1, 1000);
        console.setTextColor(Console.ANSI_RESET);
        console.showCursor();
    }
}
