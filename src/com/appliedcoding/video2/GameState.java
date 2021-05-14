package com.appliedcoding.video2;

public class GameState {
    private boolean isGameOver;
    private boolean isWin;

    public GameState(boolean isGameOver, boolean isWin) {
        this.isGameOver = isGameOver;
        this.isWin = isWin;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isWin() {
        return isWin;
    }
}
