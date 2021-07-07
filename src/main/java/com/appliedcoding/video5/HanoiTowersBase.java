package com.appliedcoding.video5;

import com.appliedcoding.io.Console;
import com.appliedcoding.io.Position;

import java.util.ArrayDeque;
import java.util.Deque;

public class HanoiTowersBase {

    public static final int DISKS = 5;

    @SuppressWarnings("unchecked")
    protected final Deque<Disk>[] towers = new ArrayDeque[]{new ArrayDeque(), new ArrayDeque(), new ArrayDeque()};
    protected Console console;
    protected TowerCanvas canvas;

    public HanoiTowersBase() {
        for (int i = 0; i < DISKS; i++) {
            towers[0].push(new Disk(DISKS - i));
        }
    }

    protected void initialize() {
        console = new Console();
        console.clear();
        console.hideCursor();
        console.enterCharacterMode();

        Position maxScreen = console.detectScreenSize();
        canvas = new TowerCanvas(console, new Position(3, 2),
                maxScreen.getX() - 4, (25 + DISKS) / 2 * 2, towers[0]);
        canvas.setBackgroundColor(15); // bright white
    }

    protected void cleanup() {
        canvas.printState(towers);
        console.enterLineMode();
        canvas.cleanup();
    }
}
