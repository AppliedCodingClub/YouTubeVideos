package com.appliedcoding.snakegame.config;

import com.appliedcoding.snakegame.model.PopulationType;

public class Configuration {

    public static final int POPULATION_SIZE = 300;
    public static final int TOP_PARENTS = (int) (0.1 * POPULATION_SIZE);
    public static final String NEURAL_NETWORK_FOLDER = "/Users/mihai/Projects/AppliedCoding/AppliedCoding/res/";
    public static final double MUTATION_RATE_FACTOR = 1.25;

    //    public static double MUTATION_RATE = 0.15; // 0.0078125; // 1/(12x8 + 8x4) = 0,0078125
//    public static double MUTATION_RATE = 0.1; // 0.007352; // 1/(13x8 + 8x4) = 0,007352941176
//    public static double MUTATION_RATE = 0.005; // 1/(13x8 + 8x8 + 8x4) = 0,005
//    public static double MUTATION_RATE = 0.03125; // 1/(5x4 + 4x3) = 0,03125
    public static double MUTATION_RATE = 0;
    public static double MUTATION_STRENGTH = 0.1;

    public static int CANVAS_WIDTH = 40; // must be even
    public static int FOOD_COUNT_TARGET = 9;
    public static int FOOD_CREDITS = 300;
    public static int STATS_UPDATE_RATE = 2;
    public static int PLAY_UPDATE_RATE = 10;

    public static boolean CANVAS_ENABLED = false;
    public static boolean FRAME_BY_FRAME = false;
    public static boolean LEARN_ENABLED = false;
    public static boolean OBSTACLES_ENABLED = false;
    public static boolean PLAY_BEST_ENABLED = false;
    public static boolean PAUSE_ENABLED = false;
    public static boolean SAVE_ENABLED = false;
    public static boolean FITNESS_EXPLORATION = true;
    public static PopulationType POPULATION_TYPE = PopulationType.Type1;
}
