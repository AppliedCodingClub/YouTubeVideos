package com.appliedcoding.snakegame.controller;

import com.appliedcoding.snakegame.config.Configuration;
import com.appliedcoding.snakegame.config.SavedConfig;
import com.appliedcoding.snakegame.config.SavedState;
import com.appliedcoding.snakegame.io.Console;
import com.appliedcoding.snakegame.model.GameState;
import com.appliedcoding.snakegame.model.PopulationState;
import com.appliedcoding.snakegame.utils.Utils;
import com.appliedcoding.snakegame.view.Canvas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Population {

    private final ThreadPoolExecutor executor;
    private Console console;
    private List<Game> games;
    private PopulationState populationState;
    private int winTotalCount;
    private int generation;
    private int winGenerationCount;
    private boolean isPopulationRunning;
    private Game bestGame;
    private GeneticAlgorithm geneticAlgorithm;
    private SavedConfig savedConfig;

    public Population(Canvas canvas) {
        games = new ArrayList<>(Configuration.POPULATION_SIZE);
        console = new Console();
        geneticAlgorithm = new GeneticAlgorithm(games, canvas, console);
        SavedState savedState = SavedState.getInstance();
        savedConfig = savedState.getConfig();
        savedConfig.setEnvironmentWidth(canvas.getWidth());
        savedConfig.setEnvironmentHeight(canvas.getHeight());
        populationState = new PopulationState();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    public void playGeneration() throws IOException {
        isPopulationRunning = true;
        winGenerationCount = 0;

        executePlaySequential();
//        executePlayParallel();

        for (int i = 0; i < Configuration.POPULATION_SIZE && isPopulationRunning; i++) {
            Game game = games.get(i);
            GameState gameState = game.getGameState();
            isPopulationRunning = !gameState.isAborted();

            if (gameState.isWin()) {
                winGenerationCount++;
                winTotalCount++;
                savedConfig.setWins(winTotalCount);
                savedConfig.setWinRx((double) winTotalCount / (generation + 1) / Configuration.POPULATION_SIZE);
            }

            savedConfig.updateMaxFitness(gameState.getFitness());
            savedConfig.updateMaxFood(gameState.getFoodCount());
        }

        if (generation % Configuration.STATS_UPDATE_RATE == 0) {
            updateStateConfig();
            endOfPlayStats();
        }

        populationState.setRunning(isPopulationRunning);
    }

    public void evolve() throws IOException {
        geneticAlgorithm.evolve();
        bestGame = games.get(0);

        if (generation > 4 && winGenerationCount > savedConfig.getWinsPerGenMax()) {
            savedConfig.setWinsPerGenMax(winGenerationCount);
            if (winGenerationCount > 50 && Configuration.LEARN_ENABLED) {
                Configuration.MUTATION_RATE /= Configuration.MUTATION_RATE_FACTOR;
                if (Configuration.SAVE_ENABLED) {
                    SavedState savedState = SavedState.getInstance();
                    savedState.setNeuralNetwork(bestGame.getNeuralNetwork());
                    savedState.saveToFile();
                }
            }
        }

        generation++;
    }

    private void executePlaySequential() throws IOException {
        int updateRate = Configuration.POPULATION_SIZE / 23;

        for (int i = 0; i < Configuration.POPULATION_SIZE && isPopulationRunning; i++) {
            Game game = games.get(i);

            if (Configuration.CANVAS_ENABLED) {
                detailedStats(i);
            }

            game.playGame();

            if (i % updateRate == 0) {
                int progress = (i + 1) * 100 / Configuration.POPULATION_SIZE;
                console.printAt(String.format("%3d%%", progress), 12, 2);
            }

            isPopulationRunning = !game.getGameState().isAborted();
        }

        console.printAt("    ", 12, 2);
    }

    private void updateStateConfig() {
        savedConfig.setFitnessExplore(Configuration.FITNESS_EXPLORATION);
        savedConfig.setGeneration(generation);
        savedConfig.setMutationRx(Configuration.MUTATION_RATE);
        savedConfig.setMutationStrength(Configuration.MUTATION_STRENGTH);
        savedConfig.setObstacleEnabled(Configuration.OBSTACLES_ENABLED);
        savedConfig.setLearnEnabled(Configuration.LEARN_ENABLED);
    }

    private void executePlayParallel() {
        int numberOfPartitions = 2;
        List<List<Game>> partitions = Utils.partition(games, numberOfPartitions);
        Task[] tasks = new Task[numberOfPartitions];

        for (int i = 0; i < numberOfPartitions; i++) {
            tasks[i] = new Task(partitions.get(i));
            executor.execute(tasks[i]);
        }

        boolean isLoop;
        do {
            isLoop = false;
            String progress = "";
            for (Task task : tasks) {
                progress += String.format("%3d%% ", task.getProgress());
                if (task.isRunning()) {
                    isLoop = true;
                }
            }

            console.printAt("Progress: " + progress, 1, 7);

            if (isLoop) {
                Utils.pause(500);
            }
        } while (isLoop);
    }

    private void endOfPlayStats() {
        console.setTextColor(Console.ANSI_RESET);
        console.printAt(String.format("%s:%.3e MaxFood:%-5d Mut/Snk:%.3f ",
                Configuration.FITNESS_EXPLORATION ? "ExFit" : "OpFit", savedConfig.getMaxFitness(),
                savedConfig.getMaxFood(), (double) geneticAlgorithm.getMutationGenerationCount() /
                        (Configuration.POPULATION_SIZE - Configuration.TOP_PARENTS)), 1, 3);
        console.printAt(String.format("MutRx:%.3e Win:%-5d %2.0f%% Win/Gen:%-3d %2.0f%% wgx:%-3d",
                Configuration.MUTATION_RATE, savedConfig.getWins(), 100.0 * savedConfig.getWinRx(),
                winGenerationCount, 100.0 * winGenerationCount / Configuration.POPULATION_SIZE,
                savedConfig.getWinsPerGenMax()), 1, 4);
    }

    private void detailedStats(int snakeId) {
        console.setTextColor(Console.ANSI_RESET);
        console.printAt(String.format("Snake:%-6d", snakeId), 17, 2);
        endOfPlayStats();
    }

    public PopulationState getPopulationState() {
        return populationState;
    }

    public void shutdown() {
        executor.shutdown();
        while (!executor.isTerminated()) {
            Utils.pause(1);
        }
    }

    public Game getBestGame() {
        return bestGame;
    }
}
