package com.appliedcoding.snakegame.model;

import com.appliedcoding.snakegame.controller.Game;

import java.util.Comparator;

public class GameFitnessComparator implements Comparator<Game> {

    @Override
    public int compare(Game game1, Game game2) {
        double fitness1 = game1.getGameState().getFitness();
        double fitness2 = game2.getGameState().getFitness();

        if (fitness2 > fitness1) {
            return 1;
        } else if (fitness1 == fitness2) {
            return 0;
        } else {
            return -1;
        }
    }
}
