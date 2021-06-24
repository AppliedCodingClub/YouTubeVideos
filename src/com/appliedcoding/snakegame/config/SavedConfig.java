package com.appliedcoding.snakegame.config;

import com.appliedcoding.snakegame.model.PopulationType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SavedConfig {

    private int generation;
    private int canvasHeight;
    private int maxFood;
    private double maxFitness;
    private int wins;
    private double winRx;
    private int winsPerGenMax;

    public boolean isAutoSaveEnabled() {
        return Configuration.SAVE_ENABLED;
    }

    public void setAutoSaveEnabled(boolean autoSave) {
        Configuration.SAVE_ENABLED = autoSave;
    }

    public int getEnvironmentWidth() {
        return Configuration.CANVAS_WIDTH;
    }

    public void setEnvironmentWidth(int canvasWidth) {
        Configuration.CANVAS_WIDTH = canvasWidth;
    }

    public int getEnvironmentHeight() {
        return canvasHeight;
    }

    public void setEnvironmentHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public boolean isFitnessExplore() {
        return Configuration.FITNESS_EXPLORATION;
    }

    public void setFitnessExplore(boolean fitnessExplore) {
        Configuration.FITNESS_EXPLORATION = fitnessExplore;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generations) {
        this.generation = generations;
    }

    public boolean isLearnEnabled() {
        return Configuration.LEARN_ENABLED;
    }

    public void setLearnEnabled(boolean learn) {
        Configuration.LEARN_ENABLED = learn;
    }

    public int getMaxFood() {
        return maxFood;
    }

    public void updateMaxFood(int maxFood) {
        this.maxFood = Math.max(this.maxFood, maxFood);
    }

    public double getMaxFitness() {
        return maxFitness;
    }

    public void updateMaxFitness(double maxFitness) {
        this.maxFitness = Math.max(this.maxFitness, maxFitness);
    }

    public double getMutationRx() {
        return Configuration.MUTATION_RATE;
    }

    public void setMutationRx(double mutationRx) {
        Configuration.MUTATION_RATE = mutationRx;
    }

    public double getMutationStrength() {
        return Configuration.MUTATION_STRENGTH;
    }

    public void setMutationStrength(double mutationStrength) {
        Configuration.MUTATION_STRENGTH = mutationStrength;
    }

    public boolean isObstacleEnabled() {
        return Configuration.OBSTACLES_ENABLED;
    }

    public void setObstacleEnabled(boolean obstacles) {
        Configuration.OBSTACLES_ENABLED = obstacles;
    }

    public PopulationType getPopulationType() {
        return Configuration.POPULATION_TYPE;
    }

    public void setPopulationType(PopulationType populationType) {
        Configuration.POPULATION_TYPE = populationType;
    }

    public boolean isPlayBestEnabled() {
        return Configuration.PLAY_BEST_ENABLED;
    }

    public void setPlayBestEnabled(boolean playBest) {
        Configuration.PLAY_BEST_ENABLED = playBest;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public double getWinRx() {
        return winRx;
    }

    public void setWinRx(double winRx) {
        this.winRx = winRx;
    }

    public int getWinsPerGenMax() {
        return winsPerGenMax;
    }

    public void setWinsPerGenMax(int winsPerGen) {
        this.winsPerGenMax = Math.max(this.winsPerGenMax, winsPerGen);
    }
}
