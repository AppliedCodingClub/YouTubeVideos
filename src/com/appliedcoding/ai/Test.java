package com.appliedcoding.ai;

import com.appliedcoding.io.Console;
import com.appliedcoding.utils.Utils;

import java.util.Random;

public class Test {

    public static void main(String[] args) {
        Console console = new Console();
        console.clear();

        NeuralNetwork neuralNetwork = new NeuralNetwork(4, 3, 2);
        System.out.println(neuralNetwork);

        Random random = new Random();
        int counter = 0;
        double[] inputs = new double[4];
        while (counter++ < 10) {
            for (int i = 0; i < inputs.length; i++) {
                inputs[i] += random.nextGaussian() * 0.5;
            }

            neuralNetwork.process(inputs);
            System.out.println(neuralNetwork);
            Utils.pause(500);
        }
    }
}
