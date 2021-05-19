package com.appliedcoding.snakegame;

public class GameState {
    private boolean isGameOver;
    private boolean isWin;
    private boolean hasFoundFood;

    public void setGameOver() {
        isGameOver = true;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setWin() {
        isWin = true;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setFoundFood() {
        hasFoundFood = true;
    }

    public boolean hasFoundFood() {
        return hasFoundFood;
    }
}
