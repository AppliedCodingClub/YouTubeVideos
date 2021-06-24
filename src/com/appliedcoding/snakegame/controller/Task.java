package com.appliedcoding.snakegame.controller;

import java.io.IOException;
import java.util.List;

public class Task implements Runnable {

    private List<Game> games;
    private boolean running;
    private int progress;

    public Task(List<Game> games) {
        this.games = games;
        running = true;
    }

    @Override
    public void run() {
        try {
            for (int i = 0, len = games.size(); i < len; i++) {
                progress = (i + 1) * 100 / len;
                Game game = games.get(i);
                game.playGame();
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public int getProgress() {
        return progress;
    }
}
