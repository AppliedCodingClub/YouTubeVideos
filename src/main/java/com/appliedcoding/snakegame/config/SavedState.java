package com.appliedcoding.snakegame.config;

import com.appliedcoding.ai.NeuralNetwork;
import com.appliedcoding.snakegame.exception.SnakeException;
import com.appliedcoding.utils.XmlSerializer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SavedState {

    @XmlTransient
    private static SavedState instance;

    @XmlElement
    private String filename;
    @XmlElement
    private SavedConfig config;
    @XmlElement
    private NeuralNetwork neuralNetwork;

    private SavedState() {
    }

    public static SavedState getInstance() {
        if (instance == null) {
            throw new SnakeException("Singleton not initialized yet. Call loadFromFile() first");
        }
        return instance;
    }

    public static SavedState loadFromFile(String filename) {
        if (instance != null) {
            throw new SnakeException("Singleton already initialized");
        }

        try {
            instance = XmlSerializer.fromXml(new File(filename), SavedState.class);
        } catch (SnakeException e) {
            instance = new SavedState();
        }
        instance.filename = filename;

        return instance;
    }

    public void saveToFile() {
        neuralNetwork.clearActivations();
        XmlSerializer.toFileXml(this, filename);
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public SavedConfig getConfig() {
        if (config == null) {
            config = new SavedConfig();
        }

        return config;
    }

    public void setConfig(SavedConfig config) {
        this.config = config;
    }

    public String getFilename() {
        return filename;
    }
}
