package com.appliedcoding.video5;

import com.appliedcoding.io.Console;
import com.appliedcoding.snakegame.model.Position;
import com.appliedcoding.utils.Utils;

import java.util.ArrayDeque;
import java.util.Deque;

public class HanoiTowersRecursive {

    public static final int DISKS = 2;
    public static final Console console = new Console();

    @SuppressWarnings("unchecked")
    private final Deque<Disk>[] towers = new ArrayDeque[]{new ArrayDeque(), new ArrayDeque(), new ArrayDeque()};
    int moveCount;
    int maxCallStack;
    private TowerCanvas canvas;

    public HanoiTowersRecursive() {
        for (int i = DISKS - 1; i >= 0; i--) {
            towers[0].push(new Disk(i + 1));
        }
        canvas = new TowerCanvas(console, new Position(1, 1), 115, 100, towers[0]);

        canvas.setBackgroundColor(0);
        canvas.clear();

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        long t = System.currentTimeMillis();
        for (int x = 0; x < 100; x++) {
            canvas.setColor((int) (Math.random() * 255));
            canvas.line((int) (width * Math.random()), (int) (height * Math.random()),
                    (int) (width * Math.random()), (int) (height * Math.random()));
            Utils.pause(30);

            canvas.setColor((int) (Math.random() * 255));
            canvas.setFillColor((int) (Math.random() * 255));
            canvas.midptellipse((int) (width * Math.random()), (int) (height * Math.random()),
                    (int) (1 + width * Math.random() / 10), (int) (1 + height * Math.random() / 10));
//            canvas.ellipse((int) (width * Math.random()), (int) (height * Math.random()),
//                    (int) (1 + width * Math.random() / 10), (int) (1 + height * Math.random() / 10));
            Utils.pause(30);

            canvas.setColor((int) (Math.random() * 255));
            canvas.setFillColor((int) (Math.random() * 255));
//            canvas.rectangle((float) (width * Math.random()), (float) (height * Math.random()),
//                    (float) (width * Math.random()), (float) (height * Math.random()));
            canvas.rectangle((float) (width * Math.random()),
                    (float) (height * Math.random()),
                    (float) (width * (2 * Math.random() - 1) / 6),
                    (float) (height * (2 * Math.random() - 1) / 6));
            Utils.pause(30);
        }

//        canvas.setColor(200);
//        canvas.setFillColor(203);
//        canvas.setFillColor(-1);
//        canvas.ellipse(width / 2, height / 2 - 1, width / 2 - 1, height / 2 - 1);
//        canvas.midptellipse(width / 2, height / 2 - 1, width / 2 - 1, height / 2 - 1);
//        canvas.rectangle(35.52269f, 82.017334f, 34.05663f, 83.61291f);

        console.setTextColor(Console.ANSI_RESET);
        console.gotoXY(1, 1000);
        console.showCursor();

        t = System.currentTimeMillis() - t;
        System.out.println("Duration: " + (t / 1000.0) + "s");
        System.exit(0);
    }

    public static void main(String[] args) {
        HanoiTowersRecursive program = new HanoiTowersRecursive();
        program.run();
    }

    private void run() {
        moveTower(0, 2, DISKS, 1);

        canvas.printState(towers);
        canvas.cleanup();
    }

    private void moveTower(int fromTower, int toTower, int disks, int depth) {
        maxCallStack = Math.max(maxCallStack, depth);

        if (disks == 1) {
            moveDisk(fromTower, toTower);
            return;
        }

        int otherTower = 3 - fromTower - toTower;
        moveTower(fromTower, otherTower, disks - 1, depth + 1);
        moveDisk(fromTower, toTower);
        moveTower(otherTower, toTower, disks - 1, depth + 1);
    }

    private void moveDisk(int fromTower, int toTower) {
        canvas.printMove(towers, fromTower, toTower);
        moveCount++;

        Disk disk = towers[fromTower].pop();
        towers[toTower].push(disk);
    }
}
