package com.appliedcoding.ai;

import com.appliedcoding.snakegame.utils.AIUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HiddenLayer {

    @XmlElement
    private double[][] weights;
    @XmlElement
    private double[] biases;

    public HiddenLayer() {
        //for deserialization
    }

    public HiddenLayer(int currentLayerSize, int previousLayerSize) {
        weights = new double[currentLayerSize][previousLayerSize];
        biases = new double[currentLayerSize];
        initRandom();
    }

    public HiddenLayer(HiddenLayer other) {
        double[][] otherWeights = other.weights;
        double[] otherBiases = other.biases;
        weights = new double[otherWeights.length][otherWeights[0].length];
        biases = new double[otherBiases.length];

        for (int i = 0; i < weights.length; i++) {
            biases[i] = otherBiases[i];
            System.arraycopy(otherWeights[i], 0, weights[i], 0, weights[0].length);
        }
    }

    private void initRandom() {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                weights[i][j] = 2 * Math.random() - 1; // [-1 .. 1)
            }
        }

        for (int i = 0; i < biases.length; i++) {
            biases[i] = 2 * Math.random() - 1; // [-1 .. 1)
        }
    }

    public double[] multiplyAddBias(double[] activation) {
        if (activation.length != weights[0].length) {
            throw new IllegalArgumentException("Invalid activation length " + activation.length);
        }

        double[] result = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            double sum = 0;
            for (int j = 0; j < weights[0].length; j++) {
                sum += activation[j] * weights[i][j];
            }
            sum += biases[i];
            result[i] = sum;
        }

        return result;
    }

    @Override
    public String toString() {
        String title = String.format("HiddenLayer[%d][%d]:", weights.length, weights[0].length);
        return AIUtils.prettyPrintLayer(title, weights, biases);
    }

    public double[][] getWeights() {
        return weights;
    }

    public double[] getBiases() {
        return biases;
    }
}
