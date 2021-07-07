package com.appliedcoding.snakegame.controller;

import com.appliedcoding.ai.NeuralNetwork;
import com.appliedcoding.io.Console;
import com.appliedcoding.snakegame.config.Configuration;
import com.appliedcoding.snakegame.config.SavedState;
import com.appliedcoding.snakegame.exception.SnakeException;
import com.appliedcoding.snakegame.model.PopulationState;
import com.appliedcoding.snakegame.model.PopulationType;
import com.appliedcoding.io.Position;
import com.appliedcoding.snakegame.view.Canvas;
import com.appliedcoding.utils.Utils;

import java.io.IOException;

public class SnakeGameAI {

    private Console console;
    private Canvas canvas;
    private boolean isGARunning;

    public SnakeGameAI(String[] args) {
        loadSavedState(args);
        console = Utils.setupConsole();
        createCanvas();
    }

    public static void main(String[] args) {
        SnakeGameAI snakeGameAI = new SnakeGameAI(args);
        try {
            snakeGameAI.play();
        } catch (IOException e) {
            snakeGameAI.resetConsole();
            e.printStackTrace(System.err);
        } finally {
            snakeGameAI.resetConsole();
        }
    }

    public void play() throws IOException {
        Population population = new Population(canvas);
        isGARunning = true;
        long start = System.currentTimeMillis();
        printStats(0, start + 1000);

        for (int generation = 0; generation < 10000 && isGARunning; generation++) {
            //play
            population.playGeneration();
            PopulationState populationState = population.getPopulationState();
            isGARunning = populationState.isRunning();

            printStats(generation, System.currentTimeMillis() - start);

            if (isGARunning) {
                // genetic algorithm
                population.evolve();

                Game bestGame = population.getBestGame();
//                saveState(bestGame.getNeuralNetwork());
                playGame(bestGame, generation);
            }
        }

        long durationSeconds = (System.currentTimeMillis() - start) / 1000;
        console.printAt(Console.ANSI_BLUE_BACKGROUND + Console.ANSI_BRIGHT_YELLOW +
                String.format("Total duration: %dm%ds", durationSeconds / 60, durationSeconds % 60), 1, 8);
        population.shutdown();
    }

    private void playGame(Game game, int generation) throws IOException {
        if (!Configuration.PLAY_BEST_ENABLED || generation % Configuration.PLAY_UPDATE_RATE != 0) {
            return;
        }

        Configuration.CANVAS_ENABLED = true;
        game.playGame();
        Configuration.CANVAS_ENABLED = false;

        if (game.getGameState().isAborted()) {
            isGARunning = false;
        }
    }

    private void loadSavedState(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("file")) {
                if (i == args.length - 1) {
                    throw new SnakeException("Invalid number of arguments. Expected filename after 'file'");
                }
                SavedState.loadFromFile(Configuration.NEURAL_NETWORK_FOLDER + args[i + 1]);
                break;
            }
        }

        setParameters(args);
    }

    private void saveState(NeuralNetwork neuralNetwork) {
        if (neuralNetwork.getGenerationVersion() % Configuration.STATS_UPDATE_RATE == 0 && Configuration.SAVE_ENABLED) {
            SavedState savedState = SavedState.getInstance();
            savedState.setNeuralNetwork(neuralNetwork);
            savedState.saveToFile();
        }
    }

    private void printStats(int generation, long durationMillis) {
        console.setTextColor(Console.ANSI_RESET);
        String s = String.format("(A)utoSave:%1s ", Configuration.SAVE_ENABLED ? "Y" : "N") +
                String.format("(L)earn:%1s ", Configuration.LEARN_ENABLED ? "Y" : "N") +
                String.format("(O)bstacle:%1s ", Configuration.OBSTACLES_ENABLED ? "Y" : "N") +
                String.format("(P)lay:%1s", Configuration.PLAY_BEST_ENABLED ? "Y" : "N");
        console.printAt(s, 1, 1);
        console.printAt(String.format("Gen:%-11d Gen/min:%-5.0f sec/Gen:%-12.1f", generation,
                (generation + 1.0) / durationMillis * 1000 * 60,
                durationMillis / 1000.0 / (generation + 1.0)), 1, 2);
    }

    public void createCanvas() {
//        Position maxScreen = console.detectScreenSize();
        Position maxScreen = new Position(115, 66);
        Position canvasTopLeft = new Position((maxScreen.getX() + 1) / 2, 1);
//        canvas = new Canvas(console, canvasTopLeft, maxScreen);
        canvas = new Canvas(console, canvasTopLeft, new Position(canvasTopLeft.getX() + Configuration.CANVAS_WIDTH - 1,
                canvasTopLeft.getY() + Configuration.CANVAS_WIDTH / 2 - 1));
    }

    private void setParameters(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "auto":
                case "autosave":
                case "save":
                    Configuration.SAVE_ENABLED = true;
                    break;

                case "canvas":
                    Configuration.CANVAS_ENABLED = true;
                    break;

                case "ex":
                case "exp":
                case "expl":
                case "explore":
                    Configuration.FITNESS_EXPLORATION = true;
                    break;

                case "learn":
                    Configuration.LEARN_ENABLED = true;
                    break;

                case "mut":
                case "mutat":
                case "mutation":
                    String mutationValue = args[++i];
                    if ("default".equals(mutationValue)) {
                        Configuration.MUTATION_RATE = 0;
                    } else {
                        Configuration.MUTATION_RATE = Double.valueOf(mutationValue);
                    }
                    break;

                case "obs":
                case "obst":
                case "obstac":
                case "obstacle":
                case "obstacles":
                    Configuration.OBSTACLES_ENABLED = true;
                    break;

                case "opt":
                case "optim":
                case "optimal":
                case "optimize":
                    Configuration.FITNESS_EXPLORATION = false;
                    break;

                case "play":
                    Configuration.PLAY_BEST_ENABLED = true;
                    break;

                case "type":
                    Configuration.POPULATION_TYPE = PopulationType.valueOf("Type" + args[++i]);
                    break;
            }
        }
    }

    public void resetConsole() {
        console.gotoXY(1, 1000);
        console.setTextColor(Console.ANSI_RESET);
        console.showCursor();
        console.enterLineMode();
    }
}
