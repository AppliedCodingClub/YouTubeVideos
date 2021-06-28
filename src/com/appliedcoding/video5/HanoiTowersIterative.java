package com.appliedcoding.video5;

import com.appliedcoding.io.Console;
import com.appliedcoding.snakegame.model.Position;

import java.util.ArrayDeque;
import java.util.Deque;

public class HanoiTowersIterative {

    public static final int DISKS = 3;
    public static final Console console = new Console();

    @SuppressWarnings("unchecked")
    private final Deque<Disk>[] towers = new ArrayDeque[]{new ArrayDeque(), new ArrayDeque(), new ArrayDeque()};
    int moveCount;
    int maxCallStack;
    private TowerCanvas canvas;

    public HanoiTowersIterative() {
        for (int i = 0; i < DISKS; i++) {
            towers[0].push(new Disk(DISKS - i));
        }
        canvas = new TowerCanvas(console, new Position(1, 1), 115, 66, towers[0]);
    }

    public static void main(String[] args) {
        HanoiTowersIterative program = new HanoiTowersIterative();
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
