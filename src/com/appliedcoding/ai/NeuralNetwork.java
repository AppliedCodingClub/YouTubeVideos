package com.appliedcoding.ai;

import com.appliedcoding.snakegame.exception.SnakeException;
import com.appliedcoding.snakegame.utils.AIUtils;
import com.appliedcoding.snakegame.utils.XmlSerializer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NeuralNetwork {

    @XmlElement(name = "generations")
    private int generationVersion;
    @XmlElement
    private HiddenLayer[] hiddenLayers;
    @XmlElement
    private double[][] activations;
    @XmlTransient
    private int mutationCount;
    @XmlTransient
    private String filename;

    private NeuralNetwork() {
        //for deserialization
    }

    /**
     * layers[0] - inputs
     * layers[layers.length - 1] - ouputs
     * all others are hidden layers
     */
    public NeuralNetwork(int... layers) {
        activations = new double[layers.length][];
        hiddenLayers = new HiddenLayer[layers.length - 1];

        activations[0] = new double[layers[0]];

        for (int i = 1; i < layers.length; i++) {
            activations[i] = new double[layers[i]];
            hiddenLayers[i - 1] = new HiddenLayer(layers[i], layers[i - 1]);
        }
    }

    public NeuralNetwork(NeuralNetwork other) {
        generationVersion = other.generationVersion;
        filename = other.filename;

        double[][] otherActivations = other.activations;
        activations = new double[otherActivations.length][];
        activations[0] = new double[otherActivations[0].length];

        HiddenLayer[] otherHiddenLayers = other.hiddenLayers;
        hiddenLayers = new HiddenLayer[activations.length - 1];

        for (int i = 1; i < activations.length; i++) {
            activations[i] = new double[otherActivations[i].length];
            hiddenLayers[i - 1] = new HiddenLayer(otherHiddenLayers[i - 1]);
        }
    }

    public NeuralNetwork(String filename) {
        this(XmlSerializer.fromXml(new File(filename), NeuralNetwork.class));
        this.filename = filename;
    }

    public static NeuralNetwork build(String filename, int... layers) {
        NeuralNetwork result;
        try {
            File file = new File(filename);
            result = XmlSerializer.fromXml(file, NeuralNetwork.class);
        } catch (SnakeException e) {
            result = new NeuralNetwork(layers);
        }

        result.setFileName(filename);
        return result;
    }

    public void process(double[] inputs) {
        if (inputs.length != activations[0].length) {
            throw new IllegalArgumentException("Invalid number of inputs " + inputs.length);
        }

        System.arraycopy(inputs, 0, activations[0], 0, inputs.length);

        for (int i = 0; i < activations.length - 1; i++) {
            activations[i + 1] = hiddenLayers[i].multiplyAddBias(activations[i]);
            relu(activations[i + 1]);
//            sigmoid(activations[i + 1]);
        }
    }

    private void relu(double[] activation) {
        for (int i = 0; i < activation.length; i++) {
            activation[i] = Math.max(0, activation[i]);
        }
    }

    private void sigmoid(double[] activation) {
        for (int i = 0; i < activation.length; i++) {
            activation[i] = 1.0 / (1 + Math.exp(-activation[i]));
        }
    }

    public NeuralNetwork crossOver(NeuralNetwork other) {
        NeuralNetwork clone = new NeuralNetwork(this); // copy constructor -- clone 'this'

        HiddenLayer[] hiddenLayers = clone.hiddenLayers;
        HiddenLayer[] otherHiddenLayers = other.getHiddenLayers();

        for (int layer = 0; layer < hiddenLayers.length; layer++) {
            HiddenLayer hiddenLayer = hiddenLayers[layer];
            HiddenLayer otherHiddenLayer = otherHiddenLayers[layer];

            double[][] weights = hiddenLayer.getWeights();
            double[][] otherWeights = otherHiddenLayer.getWeights();

            int rows = weights.length;
            int cols = weights[0].length;
            int weightSize = rows * cols;

            for (int crossOverPoint = (int) (Math.random() * weightSize); crossOverPoint < weightSize;
                 crossOverPoint++) {
                weights[crossOverPoint / cols][crossOverPoint % cols] =
                        otherWeights[crossOverPoint / cols][crossOverPoint % cols];
            }

            double[] biases = hiddenLayer.getBiases();
            double[] otherBiases = otherHiddenLayer.getBiases();
            for (int crossOverPoint = (int) (Math.random() * biases.length); crossOverPoint < biases.length;
                 crossOverPoint++) {
                biases[crossOverPoint] = otherBiases[crossOverPoint];
            }
        }

        return clone;
    }

    public void mutate(double rate, double strength) {
        Random random = new Random();

        for (HiddenLayer hiddenLayer : hiddenLayers) {
            double[][] weights = hiddenLayer.getWeights();
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[0].length; j++) {
                    if (Math.random() < rate) {
                        weights[i][j] += random.nextGaussian() * strength;
                        mutationCount++;
                    }
                }
            }

            double[] biases = hiddenLayer.getBiases();
            for (int i = 0; i < biases.length; i++) {
                if (Math.random() < rate) {
                    biases[i] += random.nextGaussian() * strength;
                    mutationCount++;
                }
            }
        }
    }

    public void saveToFile(String filename) {
        clearActivations();
        XmlSerializer.toFileXml(this, filename);
    }

    public void clearActivations() {
        for (int i = 0; i < activations.length; i++) {
            for (int j = 0; j < activations[i].length; j++) {
                activations[i][j] = 0;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream printStream = new PrintStream(baos)) {
            AIUtils.printActivations(activations, printStream);
        }

        result.append(new String(baos.toByteArray(), StandardCharsets.UTF_8));

        for (HiddenLayer layer : hiddenLayers) {
            result.append(layer.toString());
        }

        return result.toString();
    }

    public double[] getOutputs() {
        return activations[activations.length - 1];
    }

    public int getActivatedOutput() {
        double[] outputs = getOutputs();
        int result = 0;
        double maxOutput = outputs[0];

        for (int i = 1; i < outputs.length; i++) {
            if (outputs[i] > maxOutput) {
                maxOutput = outputs[i];
                result = i;
            }
        }

        return maxOutput == 0 ? -1 : result;
    }

    public HiddenLayer[] getHiddenLayers() {
        return hiddenLayers;
    }

    public int getGenerationVersion() {
        return generationVersion;
    }

    public void incGenerationVersion() {
        generationVersion++;
    }

    public double[][] getActivations() {
        return activations;
    }

    public int getMutationCount() {
        return mutationCount;
    }

    public void setMutationCount(int mutationCount) {
        this.mutationCount = mutationCount;
    }

    public void setFileName(String fileName) {
        this.filename = fileName;
    }
}