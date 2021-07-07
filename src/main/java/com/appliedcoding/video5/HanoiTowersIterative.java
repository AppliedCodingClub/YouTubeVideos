package com.appliedcoding.video5;

public class HanoiTowersIterative extends HanoiTowersBase {

//    public static final int DISKS = 5;
//
//    @SuppressWarnings("unchecked")
//    private final Deque<Disk>[] towers = new ArrayDeque[]{new ArrayDeque(), new ArrayDeque(), new ArrayDeque()};
//    public Console console;
//    int moveCount;
//    private TowerCanvas canvas;

//    public HanoiTowersIterative() {
//        for (int i = 0; i < DISKS; i++) {
//            towers[0].push(new Disk(DISKS - i));
//        }
//    }

    public static void main(String[] args) {
        HanoiTowersIterative program = new HanoiTowersIterative();

        try {
            program.initialize();
            program.run();
        } finally {
            program.cleanup();
        }
    }

    private void run() {
        moveTower(0, 2);
    }

    private void moveTower(int fromTower, int toTower) {
        int otherTower = 3 - fromTower - toTower;
        if (DISKS % 2 == 0) {
            int aux = otherTower;
            otherTower = toTower;
            toTower = aux;
        }

        int n = (int) (Math.pow(2, DISKS) - 1);
        Configuration.IS_RUNNING = true;

        for (int i = 0; i < n && Configuration.IS_RUNNING; i++) {
            if (i % 3 == 0) {
                moveDisk(fromTower, toTower);
            } else if (i % 3 == 1) {
                moveDisk(fromTower, otherTower);
            } else {
                moveDisk(otherTower, toTower);
            }
        }
    }

    private void moveDisk(int fromTower, int toTower) {
        if (!isValidMove(fromTower, toTower)) {
            int aux = fromTower;
            fromTower = toTower;
            toTower = aux;
        }

        canvas.printMove(towers, fromTower, toTower);

        Disk disk = towers[fromTower].pop();
        towers[toTower].push(disk);
    }

    private boolean isValidMove(int fromTower, int toTower) {
        if (towers[toTower].size() == 0) {
            return true;
        }

        if (towers[fromTower].size() == 0) {
            return false;
        }

        Disk disk = towers[fromTower].peek();
        return disk.getSize() < towers[toTower].peek().getSize();
    }
}
