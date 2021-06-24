package com.appliedcoding.snakegame.controller;

import com.appliedcoding.ai.NeuralNetwork;
import com.appliedcoding.io.Console;
import com.appliedcoding.io.KeyboardUtils;
import com.appliedcoding.snakegame.config.Configuration;
import com.appliedcoding.snakegame.config.SavedState;
import com.appliedcoding.snakegame.model.Environment;
import com.appliedcoding.snakegame.model.GameState;
import com.appliedcoding.snakegame.view.Canvas;
import com.appliedcoding.utils.AIUtils;
import com.appliedcoding.utils.Utils;

import java.io.IOException;

public class Game {

    private boolean isGameRunning;
    private Canvas canvas;
    private Environment environment;
    private Console console;
    private NeuralNetwork neuralNetwork;
    private GameState gameState;
    private GeneticAlgorithm geneticAlgorithm;

    public Game(NeuralNetwork neuralNetwork, Canvas canvas, GeneticAlgorithm geneticAlgorithm) {
        this.neuralNetwork = neuralNetwork;
        this.canvas = canvas;
        this.geneticAlgorithm = geneticAlgorithm;
        console = new Console();
    }

    public void playGame() throws IOException {
        gameState = new GameState();
        environment = new Environment(canvas, gameState);
        canvas.paintInitialState(environment);
        isGameRunning = true;

        while (isGameRunning) {
            gameLoop();
        }
    }

    private void gameLoop() throws IOException {
        geneticAlgorithm.processGameStep(this);

        if (Configuration.PLAY_BEST_ENABLED && Configuration.CANVAS_ENABLED) {
            AIUtils.printActivations(neuralNetwork.getActivations(), 42, Configuration.CANVAS_WIDTH / 2 + 5);
        }

        while (Configuration.PAUSE_ENABLED) {
            handleKeyboard();
            Utils.pause(10);
        }

        if (Configuration.FRAME_BY_FRAME) {
            Configuration.PAUSE_ENABLED = true;
            Configuration.FRAME_BY_FRAME = false;
        } else if (Configuration.CANVAS_ENABLED) {
            Utils.pause(Utils.PAUSE);
        }

        canvas.paintRemove(environment);
        environment.moveSnake();

        if (gameState.isGameOver() || gameState.isWin()) {
            isGameRunning = false;
        }

        canvas.paint(environment);

        handleKeyboard();
        detailedStats();
    }

    private void detailedStats() {
        if (Configuration.CANVAS_ENABLED) {
            console.setTextColor(Console.ANSI_RESET);
            console.printAt(String.format("Age:%-11d Food:%-2d", gameState.getSteps(),
                    gameState.getFoodCount()), 1, 5);
            console.printAt(String.format("Credit:%-8d Fitness:%.3e ", gameState.getFoodCredits(),
                    gameState.getFitness()), 1, 6);
        }
    }

    private void handleKeyboard() throws IOException {
        switch (KeyboardUtils.readKeyPress()) {
            case KeyboardUtils.A_KEY: //toggle autosave to disk for neural network
                Configuration.SAVE_ENABLED = !Configuration.SAVE_ENABLED;
                SavedState.getInstance().saveToFile();
                break;

            case KeyboardUtils.ESC_KEY: //quit
                isGameRunning = false;
                gameState.setAborted(true);
                break;

            case KeyboardUtils.PLUS_KEY:
            case KeyboardUtils.EQUALS_KEY: //faster
                Utils.PAUSE /= 1.5;
                break;

            case KeyboardUtils.MINUS_KEY: //slower
                Utils.PAUSE = (int) Math.max(2, Utils.PAUSE * 1.5);
                Utils.PAUSE = Math.min(Utils.PAUSE, 5000);
                break;

            case KeyboardUtils.C_LOWER_KEY: //paint canvas
                Configuration.CANVAS_ENABLED = !Configuration.CANVAS_ENABLED;
                break;

            case KeyboardUtils.F_KEY: //one frame forward
                if (Configuration.PAUSE_ENABLED) {
                    Configuration.PAUSE_ENABLED = false;
                    Configuration.FRAME_BY_FRAME = true;
                }
                break;

            case KeyboardUtils.L_KEY: //toggle learning mode (vs read-only) neural network
                Configuration.LEARN_ENABLED = !Configuration.LEARN_ENABLED;
                break;

            case KeyboardUtils.M_LOWER_KEY: //more mutation
                Configuration.MUTATION_RATE = Math.min(1,
                        Configuration.MUTATION_RATE * Configuration.MUTATION_RATE_FACTOR);
                break;

            case KeyboardUtils.M_UPPER_KEY: //less mutation
                Configuration.MUTATION_RATE = Math.max(1e-6,
                        Configuration.MUTATION_RATE / Configuration.MUTATION_RATE_FACTOR);
                break;

            case KeyboardUtils.O_KEY: //toggle obstacles
                Configuration.OBSTACLES_ENABLED = !Configuration.OBSTACLES_ENABLED;
                break;

            case KeyboardUtils.P_KEY: //toggle play best game
                Configuration.PLAY_BEST_ENABLED = !Configuration.PLAY_BEST_ENABLED;
                break;

            case KeyboardUtils.R_LOWER_KEY: //canvas bigger
                canvas.resize(2);
                break;

            case KeyboardUtils.R_UPPER_KEY: //canvas smaller
                canvas.resize(-2);
                break;

            case KeyboardUtils.SPACE_KEY: //toggle pause
                Configuration.PAUSE_ENABLED = !Configuration.PAUSE_ENABLED;
                Configuration.FRAME_BY_FRAME = false;
                break;

            case KeyboardUtils.T_KEY: //toggle fitness function
                Configuration.FITNESS_EXPLORATION = !Configuration.FITNESS_EXPLORATION;
                break;
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}