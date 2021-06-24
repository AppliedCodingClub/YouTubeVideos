package com.appliedcoding.snakegame.controller;

import com.appliedcoding.ai.NeuralNetwork;
import com.appliedcoding.io.Console;
import com.appliedcoding.snakegame.config.Configuration;
import com.appliedcoding.snakegame.config.SavedConfig;
import com.appliedcoding.snakegame.config.SavedState;
import com.appliedcoding.snakegame.model.Direction;
import com.appliedcoding.snakegame.model.Environment;
import com.appliedcoding.snakegame.model.EnvironmentObjectType;
import com.appliedcoding.snakegame.model.GameFitnessComparator;
import com.appliedcoding.snakegame.model.GameState;
import com.appliedcoding.snakegame.model.PopulationType;
import com.appliedcoding.snakegame.model.Snake;
import com.appliedcoding.snakegame.view.Canvas;
import com.appliedcoding.utils.AIUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class GeneticAlgorithm {

    public static final int[][] DIRECTIONS = new int[][]{
            {0, 1, 0, -1}, // deltaX   N E S W
            {-1, 0, 1, 0}  // deltaY   N E S W
    };

    private List<Game> games;
    private Comparator<? super Game> fitnessComparator;
    private int mutationGenerationCount;
    private Console console;
    private int generation;
    private PopulationType populationType;

    public GeneticAlgorithm(List<Game> games, Canvas canvas, Console console) {
        this.games = games;
        this.console = console;
        populationType = SavedState.getInstance().getConfig().getPopulationType();

        fitnessComparator = new GameFitnessComparator();

        NeuralNetwork neuralNetwork = buildNeuralNetwork();
        for (int i = 0; i < Configuration.POPULATION_SIZE; i++) {
            NeuralNetwork clone = new NeuralNetwork(neuralNetwork);
            games.add(new Game(clone, canvas, this));
        }
    }

    private NeuralNetwork buildNeuralNetwork() {
        NeuralNetwork result = null;

        switch (populationType) {
            case Type1:
                // Mx = 0.007142857143
                result = loadOrCreateNeuralNetwork(12, 8, 4);
                break;

            case Type2:
                // Mx = 0.01851851852
                result = loadOrCreateNeuralNetwork(9, 3, 3, 3);
                break;

            case Type3:
                result = loadOrCreateNeuralNetwork(6, 4, 4, 3);
                break;

            case Type4:
                result = loadOrCreateNeuralNetwork(13, 8, 8, 4);
                break;

            case Type5:
                result = loadOrCreateNeuralNetwork(5, 4, 3);
                break;

            case Type6:
                result = loadOrCreateNeuralNetwork(5, 4, 3);
                break;

            case Type7:
                result = loadOrCreateNeuralNetwork(6, 4, 3);
                break;
        }

        return result;
    }

    public void processGameStep(Game game) {
        Environment environment = game.getEnvironment();
        NeuralNetwork neuralNetwork = game.getNeuralNetwork();

        switch (populationType) {
            case Type1:
                neuralNetwork.process(getNeuralNetworkInputs1(game));
                updateSnakeDirection1(environment, neuralNetwork);
                break;

            case Type2:
                neuralNetwork.process(getNeuralNetworkInputs2(game));
                updateSnakeDirection2(environment, neuralNetwork);
                break;

            case Type3:
                neuralNetwork.process(getNeuralNetworkInputs3(game));
                updateSnakeDirection2(environment, neuralNetwork);
                break;

            case Type4:
                neuralNetwork.process(getNeuralNetworkInputs4(game));
                updateSnakeDirection1(environment, neuralNetwork);
                break;

            case Type5:
                neuralNetwork.process(getNeuralNetworkInputs5(game));
                updateSnakeDirection2(environment, neuralNetwork);
                break;

            case Type6:
                neuralNetwork.process(getNeuralNetworkInputs6(game));
                updateSnakeDirection2(environment, neuralNetwork);
                break;

            case Type7:
                neuralNetwork.process(getNeuralNetworkInputs7(game));
                updateSnakeDirection2(environment, neuralNetwork);
                break;
        }
    }

    public void evolve() throws IOException {
        //selection
        games.sort(fitnessComparator);
        mutationGenerationCount = 0;

        double sumFitness = 0;
        double fitnesses[] = new double[Configuration.TOP_PARENTS];

        for (int topParent = 0; topParent < Configuration.TOP_PARENTS; topParent++) {
            Game game = games.get(topParent);
            game.getNeuralNetwork().incGenerationVersion();
            GameState gameState = game.getGameState();
            double fitness = gameState.getFitness();
            fitnesses[topParent] = fitness;
            sumFitness += fitness;
        }

        if (Configuration.LEARN_ENABLED) {
            AIUtils.normalizeFitnesses(fitnesses);
            for (int kid = Configuration.TOP_PARENTS; kid < Configuration.POPULATION_SIZE; kid++) {
                int dad = selectRoulette(fitnesses, sumFitness);
                NeuralNetwork dadNN = games.get(dad).getNeuralNetwork();

                int mom = selectRoulette(fitnesses, sumFitness);
                NeuralNetwork momNN = games.get(mom).getNeuralNetwork();

                //crossover
                NeuralNetwork kidNN = dadNN.crossOver(momNN);
                games.get(kid).setNeuralNetwork(kidNN);

                //mutation
                kidNN.setMutationCount(0);
                kidNN.mutate(Configuration.MUTATION_RATE, Configuration.MUTATION_STRENGTH);
                mutationGenerationCount += kidNN.getMutationCount();
            }
        }

        topParentStats(fitnesses);
        generation++;
    }

    private int selectRoulette(double[] fitnesses, double totalFitness) {
        int result = -1;
        double fitnessSum = 0;
        double fitnessTarget = totalFitness * Math.random();

        do {
            result++;
            fitnessSum += fitnesses[result];
        } while (fitnessSum < fitnessTarget);

        return result;
    }

    private NeuralNetwork loadOrCreateNeuralNetwork(int... args) {
        SavedState savedState = SavedState.getInstance();
        NeuralNetwork neuralNetwork = savedState.getNeuralNetwork();

        if (neuralNetwork == null) {
            neuralNetwork = new NeuralNetwork(args);
            savedState.setNeuralNetwork(neuralNetwork);
        }

        SavedConfig savedConfig = savedState.getConfig();
        if (savedConfig.getMutationRx() == 0) {
            savedConfig.setMutationRx(getDefaultMutationRate(neuralNetwork));
        }

        return neuralNetwork;
    }

    public double getDefaultMutationRate(NeuralNetwork neuralNetwork) {
        int weightsCount = 0;
        double[][] activations = neuralNetwork.getActivations();
        for (int layer = 1; layer < activations.length; layer++) {
            weightsCount += activations[layer].length * (activations[layer - 1].length + 1);
        }

        return 1.0 / weightsCount;
    }

    /**
     * Inputs:
     * - [0..3] distances to obstacles
     * - [4..7] distances to snake body
     * - [8..11] distances to food
     */
    private double[] getNeuralNetworkInputs1(Game game) {
        double[] result = new double[3 * DIRECTIONS[0].length];

//        Canvas canvas = game.getCanvas();
        Environment environment = game.getEnvironment();

//        int width = canvas.getWidth();
        int index = 0;

        for (int direction = 0; direction < DIRECTIONS[0].length; direction++) {
            result[index] = environment.getDistanceTo(DIRECTIONS, direction, EnvironmentObjectType.Obstacle);
//            result[index] /= width;
            index++;
        }

        for (int direction = 0; direction < DIRECTIONS[0].length; direction++) {
            result[index] = environment.getDistanceTo(DIRECTIONS, direction, EnvironmentObjectType.Snake);
//            result[index] /= width;
            index++;
        }

        for (int direction = 0; direction < DIRECTIONS[0].length; direction++) {
            result[index] = environment.getDistanceTo(DIRECTIONS, direction, EnvironmentObjectType.Food);
//            result[index] /= width;
            index++;
        }

        return result;
    }

    /**
     * Inputs:
     * - [0] distance ahead to obstacle relative to snake head and direction
     * - [1] distance to the left to obstacle
     * - [2] distance to the right to obstacle
     * - [3] distance ahead to snake body
     * - [4] distance to the left to snake body
     * - [5] distance to the right to snake body
     * - [6] distance to food straight ahead (or 0 if no food ahead)
     * - [7] distance to food left (or 0 if no food on the left)
     * - [8] distance to food right (or 0 if no food on the right)
     */
    private double[] getNeuralNetworkInputs2(Game game) {
        double[] result = new double[9];

        Environment environment = game.getEnvironment();
        Snake snake = environment.getSnake();

        Direction straightDirection = snake.getDirection();
        Direction leftDirection = straightDirection.leftOf();
        Direction rightDirection = straightDirection.rightOf();

        int straight = straightDirection.getValue();
        int left = leftDirection.getValue();
        int right = rightDirection.getValue();

        //Obstacles
        result[0] = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Obstacle);
        result[1] = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Obstacle);
        result[2] = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Obstacle);

        //Snake
        result[3] = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Snake);
        result[4] = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Snake);
        result[5] = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Snake);

        //Food
        result[6] = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Food);
        result[7] = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Food);
        result[8] = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Food);

//        int width = canvas.getWidth();
//        result[0] /= width;
//        result[1] /= width;
//        result[2] /= width;
//        result[3] /= width;
//        result[4] /= width;
//        result[5] /= width;
//        result[6] /= width;
//        result[7] /= width;
//        result[8] /= width;

        return result;
    }

    /**
     * Inputs:
     * - [0] distance ahead to obstacle or snake body (whichever is closer) relative to snake head and direction
     * - [1] distance to the left to obstacle or snake body
     * - [2] distance to the right to obstacle or snake body
     * - [3] distance to food straight ahead (or 0 if no food ahead)
     * - [4] distance to food left (or 0 if no food on the left)
     * - [5] distance to food right (or 0 if no food on the right)
     */
    private double[] getNeuralNetworkInputs3(Game game) {
        double[] result = new double[6];

        Environment environment = game.getEnvironment();

        Direction snakeDirection = environment.getSnake().getDirection();
        Direction leftOfSnake = snakeDirection.leftOf();
        Direction rightOfSnake = snakeDirection.rightOf();

        int straight = snakeDirection.getValue();
        int left = leftOfSnake.getValue();
        int right = rightOfSnake.getValue();

        //Obstacles
        result[0] = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Obstacle);
        double distance = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Snake);
        if (result[0] == 0 || (distance > 0 && distance < result[0])) {
            result[0] = distance;
        }

        result[1] = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Obstacle);
        distance = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Snake);
        if (result[1] == 0 || (distance > 0 && distance < result[1])) {
            result[1] = distance;
        }

        result[2] = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Obstacle);
        distance = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Snake);
        if (result[2] == 0 || (distance > 0 && distance < result[2])) {
            result[2] = distance;
        }

        //Food
        result[3] = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Food);
        result[4] = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Food);
        result[5] = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Food);

//        double width = canvas.getWidth();
//        result[0] /= width;
//        result[1] /= width;
//        result[2] /= width;
//        result[3] /= width;
//        result[4] /= width;
//        result[5] /= width;

        return result;
    }

    /**
     * Inputs:
     * - [0] snake direction
     * - [1..4] distances to obstacles
     * - [5..8] distances to snake body
     * - [9..12] distances to food
     */
    private double[] getNeuralNetworkInputs4(Game game) {
        double[] result = new double[3 * DIRECTIONS[0].length + 1];

        Environment environment = game.getEnvironment();
        Snake snake = environment.getSnake();

        result[0] = snake.getDirection().getValue();

//        int width = canvas.getWidth();
        int index = 1;

        for (int direction = 0; direction < DIRECTIONS[0].length; direction++) {
            result[index] = environment.getDistanceTo(DIRECTIONS, direction, EnvironmentObjectType.Obstacle);
//            result[index] /= width;
            index++;
        }

        for (int direction = 0; direction < DIRECTIONS[0].length; direction++) {
            result[index] = environment.getDistanceTo(DIRECTIONS, direction, EnvironmentObjectType.Snake);
//            result[index] /= width;
            index++;
        }

        for (int direction = 0; direction < DIRECTIONS[0].length; direction++) {
            result[index] = environment.getDistanceTo(DIRECTIONS, direction, EnvironmentObjectType.Food);
//            result[index] /= width;
            index++;
        }

        return result;
    }

    /**
     * Inputs:
     * - [0] distance ahead to obstacle or snake body (whichever is closer) relative to snake head
     * - [1] distance to the left to obstacle or snake body (whichever is closer)
     * - [2] distance to the right to obstacle or snake body (whichever is closer)
     * - [3] distance to food
     * - [4] sine angle to food [-1..0..1] relative to snake direction
     */
    private double[] getNeuralNetworkInputs5(Game game) {
        double[] result = new double[5];

        Environment environment = game.getEnvironment();

        Direction snakeDirection = environment.getSnake().getDirection();
        Direction leftOfSnake = snakeDirection.leftOf();
        Direction rightOfSnake = snakeDirection.rightOf();

        int straight = snakeDirection.getValue();
        int left = leftOfSnake.getValue();
        int right = rightOfSnake.getValue();

        //Obstacles
        result[0] = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Obstacle);
        double distance = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Snake);
        if (result[0] == 0 || (distance > 0 && distance < result[0])) {
            result[0] = distance;
        }

        result[1] = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Obstacle);
        distance = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Snake);
        if (result[1] == 0 || (distance > 0 && distance < result[1])) {
            result[1] = distance;
        }

        result[2] = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Obstacle);
        distance = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Snake);
        if (result[2] == 0 || (distance > 0 && distance < result[2])) {
            result[2] = distance;
        }

        //Food
        result[3] = environment.getDistanceToFood();
        result[4] = environment.getSineAngleToFood();

//        double width = canvas.getWidth();
//        result[0] /= width;
//        result[1] /= width;
//        result[2] /= width;
//        result[3] /= width;
//        result[4] /= width;
//        result[5] /= width;

        return result;
    }

    /**
     * Inputs:
     * - [0] distance ahead to obstacle or snake body (whichever is closer) relative to snake head
     * - [1] distance to the left to obstacle or snake body (whichever is closer)
     * - [2] distance to the right to obstacle or snake body (whichever is closer)
     * - [3] distance to food
     * - [4] cosine angle to food [0..1..0] relative to snake direction
     */
    public double[] getNeuralNetworkInputs6(Game game) {
        double[] result = new double[5];

        Environment environment = game.getEnvironment();
        Snake snake = environment.getSnake();

        Direction snakeDirection = snake.getDirection();
        Direction leftOfSnake = snakeDirection.leftOf();
        Direction rightOfSnake = snakeDirection.rightOf();

        int straight = snakeDirection.getValue();
        int left = leftOfSnake.getValue();
        int right = rightOfSnake.getValue();

        //Obstacles
        result[0] = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Obstacle);
        double distance = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Snake);
        if (result[0] == 0 || (distance > 0 && distance < result[0])) {
            result[0] = distance;
        }

        result[1] = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Obstacle);
        distance = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Snake);
        if (result[1] == 0 || (distance > 0 && distance < result[1])) {
            result[1] = distance;
        }

        result[2] = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Obstacle);
        distance = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Snake);
        if (result[2] == 0 || (distance > 0 && distance < result[2])) {
            result[2] = distance;
        }

        //Food
        result[3] = environment.getDistanceToFood();
        result[4] = environment.getCosineAngleToFood();

//        double width = canvas.getWidth();
//        result[0] /= width;
//        result[1] /= width;
//        result[2] /= width;
//        result[3] /= width;
//        result[4] /= width;
//        result[5] /= width;

        return result;
    }

    /**
     * Inputs:
     * - [0] distance ahead to obstacle or snake body (whichever is closer) relative to snake head
     * - [1] distance to the left to obstacle or snake body (whichever is closer)
     * - [2] distance to the right to obstacle or snake body (whichever is closer)
     * - [3] heatmap value to food ahead
     * - [4] heatmap value to food to the left of snake head
     * - [5] heatmap value to food to the right
     */
    public double[] getNeuralNetworkInputs7(Game game) {
        double[] result = new double[6];

        Environment environment = game.getEnvironment();
        Snake snake = environment.getSnake();

        Direction snakeDirection = snake.getDirection();
        Direction leftOfSnake = snakeDirection.leftOf();
        Direction rightOfSnake = snakeDirection.rightOf();

        int straight = snakeDirection.getValue();
        int left = leftOfSnake.getValue();
        int right = rightOfSnake.getValue();

        //Obstacles
        result[0] = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Obstacle);
        double distance = environment.getDistanceTo(DIRECTIONS, straight, EnvironmentObjectType.Snake);
        if (result[0] == 0 || (distance > 0 && distance < result[0])) {
            result[0] = distance;
        }

        result[1] = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Obstacle);
        distance = environment.getDistanceTo(DIRECTIONS, left, EnvironmentObjectType.Snake);
        if (result[1] == 0 || (distance > 0 && distance < result[1])) {
            result[1] = distance;
        }

        result[2] = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Obstacle);
        distance = environment.getDistanceTo(DIRECTIONS, right, EnvironmentObjectType.Snake);
        if (result[2] == 0 || (distance > 0 && distance < result[2])) {
            result[2] = distance;
        }

        //Food
        result[3] = environment.getHeatMap(DIRECTIONS, straight);
        result[4] = environment.getHeatMap(DIRECTIONS, left);
        result[5] = environment.getHeatMap(DIRECTIONS, right);

//        double width = canvas.getWidth();
//        result[0] /= width;
//        result[1] /= width;
//        result[2] /= width;
//        result[3] /= width;
//        result[4] /= width;
//        result[5] /= width;

        return result;
    }

    /**
     * Each activated output neuron represents a direction 0..3
     * - 0 Up
     * - 1 Right
     * - 2 Down
     * - 3 Left
     */
    private void updateSnakeDirection1(Environment environment, NeuralNetwork neuralNetwork) {
        int activatedOutput = neuralNetwork.getActivatedOutput();
        if (activatedOutput > -1) {
            Snake snake = environment.getSnake();
            snake.setDirection(Direction.fromInt(activatedOutput));
        }
    }

    /**
     * The activated output represents the change of current direction 0..2
     * - 0 Straight - no change
     * - 1 Turn left
     * - 2 Turn right
     */
    private void updateSnakeDirection2(Environment environment, NeuralNetwork neuralNetwork) {
        int activatedOutput = neuralNetwork.getActivatedOutput();

        if (activatedOutput > 0) {
            Snake snake = environment.getSnake();
            Direction direction = snake.getDirection();
            switch (activatedOutput) {
                case 1: // turn left
                    snake.setDirection(direction.leftOf());
                    break;

                case 2: // turn right
                    snake.setDirection(direction.rightOf());
                    break;
            }
        }
    }

    private void topParentStats(double[] fitnesses) {
        if (generation % Configuration.STATS_UPDATE_RATE == 0) {
            console.setTextColor(Console.ANSI_RESET);
            for (int topParent = 0; topParent < Configuration.TOP_PARENTS; topParent++) {
                Game game = games.get(topParent);
                GameState gameState = game.getGameState();

                console.printAt(String.format("TopFit[%-2d]:%.3e C:%-3d F:%-1d A:%-4d",
                        topParent, fitnesses[topParent], gameState.getFoodCredits(), gameState.getFoodCount(),
                        gameState.getSteps()), 1, 9 + topParent);
            }
        }
    }

    public int getMutationGenerationCount() {
        return mutationGenerationCount;
    }
}
