package com.appliedcoding.snakegame.model;

import com.appliedcoding.snakegame.config.Configuration;

public class GameState {

    private boolean aborted;
    private int foodCount;
    private int foodCredits = Configuration.FOOD_CREDITS;
    private boolean foundFood;
    private boolean gameOver;
    //    private List<Direction> moves = new ArrayList<>();
    private int steps;
    private boolean win;

    public double getFitness() {
//        http://ceur-ws.org/Vol-2468/p9.pdf

        if (Configuration.FITNESS_EXPLORATION) {
            return getFitnessForExploration();
        } else {
            return getFitnessForOptimization();
        }
    }

    public double getFitnessForExploration() {
        // training fitness - promotes exploration
        double fitness = steps + foodCount * Configuration.FOOD_CREDITS;
//        double fitness = steps * steps * Math.pow(2, foodCount);

        if (foodCredits == 0) {
            fitness -= 0.95 * steps;
//        } else if (gameOver) {
//            fitness *= 0.8;
//        } else if (win) {
//            fitness *= 1.2;
        }

        return fitness;
    }

    public double getFitnessForOptimization() {
        // optimization fitness - values shorter quicker goals
        double fps = (double) foodCount / (steps + 1);
        double pow2fps = Math.pow(2, fps);
        double spf = (double) steps / (foodCount + 1);
        double fitness = spf + Math.pow(2 * foodCount * Configuration.FOOD_CREDITS, pow2fps);

        if (foodCredits == 0) {
            fitness *= 0.4;
        } else if (gameOver) {
            fitness *= 0.8;
        } else if (win) {
            fitness *= 1.2;
        }

        return fitness;
    }

    public void setGameOver() {
        gameOver = true;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setWin() {
        win = true;
    }

    public boolean isWin() {
        return win;
    }

    public void setFoundFood() {
        foundFood = true;
    }

    public boolean hasFoundFood() {
        return foundFood;
    }

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        this.aborted = aborted;
    }

    public int getFoodCredits() {
        return foodCredits;
    }

    public void setFoodCredits(int credits) {
        foodCredits = Math.max(0, credits);
    }

    public void updateFoodCredits(int amount) {
        foodCredits = Math.max(0, foodCredits + amount);
    }

    public int getSteps() {
        return steps;
    }

    public void incSteps() {
        steps++;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public void incFoodCount() {
        foodCount++;
    }

//    public List<Direction> getMoves() {
//        return moves;
//    }

//    public void addMove(Direction move) {
//        moves.add(move);
//    }
}
