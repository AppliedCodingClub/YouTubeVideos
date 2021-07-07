package com.appliedcoding.utils;

import com.appliedcoding.io.Console;

import java.io.PrintStream;
import java.util.Arrays;

public class AIUtils {

    private static final int COLUMN_WIDTH = 12;
    private static final Console console = new Console();

    public static void printActivations(double[][] activations) {
        printActivations(activations, -1, true);
    }

    public static void printActivations(double[][] activations, int x, int y) {
        printActivations(activations, -1, true, System.out, x, y);
    }

    public static void printActivations(double[][] activations, PrintStream printStream) {
        printActivations(activations, -1, true, printStream, 0, 0);
    }

    public static void printActivations(double[][] activations, int highlightLayer, boolean highlightOutputNeuron) {
        printActivations(activations, -1, true, System.out, 1, 1);
    }

    public static void printActivations(double[][] activations, int highlightLayer,
                                        boolean highlightOutputNeuron, PrintStream stream, int x, int y) {
        if (stream == System.out) {
            console.gotoXY(x, y);
        }

        int layerCount = activations.length;

        StringBuilder sb = new StringBuilder("| Nn | ");
        StringBuilder sbSeparatorLine = new StringBuilder("+----+-");

        writeActivationsHeader(layerCount, highlightLayer, sb, sbSeparatorLine);

        stream.println(sbSeparatorLine);
        if (stream == System.out) {
            console.gotoXY(x, ++y);
        }

        stream.println(sb);
        if (stream == System.out) {
            console.gotoXY(x, ++y);
        }

        stream.println(sbSeparatorLine);
        if (stream == System.out) {
            console.gotoXY(x, ++y);
        }

        boolean isNeuronFound;
        int neuron = 0;
        int activatedOutput = getActivatedOutput(activations);

        do {
            isNeuronFound = false;
            if (highlightOutputNeuron && neuron == activatedOutput) {
                sb = new StringBuilder(String.format("| " + Console.ANSI_BLUE_BACKGROUND +
                        Console.ANSI_BRIGHT_YELLOW + "%2d" + Console.ANSI_RESET + " | ", neuron));
            } else {
                sb = new StringBuilder(String.format("| %2d | ", neuron));
            }

            for (int layer = 0; layer < layerCount; layer++) {
                double[] activationLayer = activations[layer];
                if (neuron < activationLayer.length) {
                    isNeuronFound = true;
                    double activatonValue = activationLayer[neuron];
                    if (activatonValue == 0) {
                        sb.append(fillString(COLUMN_WIDTH, ' '));
                    } else {
                        String formattedValue = String.format("%" + COLUMN_WIDTH + ".3e", activatonValue);
                        if (highlightOutputNeuron && layer == layerCount - 1 && neuron == activatedOutput) {
                            formattedValue = Console.ANSI_BLUE_BACKGROUND + Console.ANSI_BRIGHT_YELLOW +
                                    formattedValue + Console.ANSI_RESET;
                        }
                        sb.append(formattedValue);
                    }
                } else {
                    sb.append(fillString(COLUMN_WIDTH, ' '));
                }
                sb.append(" | ");
            }

            if (isNeuronFound) {
                stream.println(sb);
                if (stream == System.out) {
                    console.gotoXY(x, ++y);
                }
            }

            neuron++;
        } while (isNeuronFound);

        stream.println(sbSeparatorLine);
        if (stream == System.out) {
            console.gotoXY(x, ++y);
        }

//        if (stream == System.out) {
//            Utils.pause(100);
//        }
    }

    private static void writeActivationsHeader(int layerCount, int highlightLayer,
                                               StringBuilder sb, StringBuilder sbLine) {
        for (int layer = 0; layer < layerCount; layer++) {
            String layerName = "Hidden";
            if (layer == 0) {
                layerName = "Input";
            } else if (layer == layerCount - 1) {
                layerName = "Output";
            }

            layerName = centerString(COLUMN_WIDTH, String.format("%s[%d]", layerName, layer));
            if (layer == highlightLayer) {
                layerName = Console.ANSI_BLUE_BACKGROUND + Console.ANSI_BRIGHT_YELLOW +
                        layerName + Console.ANSI_RESET;
            }

            sb.append(String.format("%" + COLUMN_WIDTH + "s", layerName));
            sbLine.append(fillString(COLUMN_WIDTH, '-'));

            if (layer < layerCount - 1) {
                sb.append(" | ");
                sbLine.append("-+-");
            } else {
                sb.append(" |");
                sbLine.append("-+");
            }
        }
    }

    // https://stackoverflow.com/questions/8154366/how-to-center-a-string-using-string-format
    // https://www.leveluplunch.com/java/examples/center-justify-string/
    public static String centerString(int width, String s) {
        return String.format("%-" + width + "s",
                String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }

    public static int getActivatedOutput(double[][] activations) {
        double[] output = activations[activations.length - 1];
        int result = 0;
        double maxOutput = output[0];

        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxOutput) {
                maxOutput = output[i];
                result = i;
            }
        }

        return maxOutput == 0 ? -1 : result;
    }

    public static String prettyPrintLayer(String title, double[][] values, double[] biases) {
        StringBuilder sb = new StringBuilder();
        buildSeparator(sb, title, values[0].length);
        sb.append(" ");
        buildSeparator(sb, "Biases:", 1);
        sb.append("\r\n");

        for (int i = 0; i < values.length; i++) {
            sb.append("|      ");
            for (int j = 0; j < values[0].length; j++) {
                sb.append(String.format("%" + COLUMN_WIDTH + ".9f | ", values[i][j]));
            }
            sb.append(String.format("|      %" + COLUMN_WIDTH + ".9f | ", biases[i]));
            sb.append("\r\n");
        }

        buildSeparator(sb, "", values[0].length);
        sb.append(" ");
        buildSeparator(sb, "", 1);
        sb.append("\r\n");

        return sb.toString();
    }

    private static String fillString(int size, char character) {
        if (size < 1) {
            return "";
        }

        char[] chars = new char[size];
        Arrays.fill(chars, character);
        return new String(chars);
    }

    private static void buildSeparator(StringBuilder sb, String title, int cols) {
        sb.append("+-");
        sb.append(title);
        sb.append(fillString(6 + COLUMN_WIDTH - title.length(), '-'));
        sb.append("+");
        for (int i = 1; i < cols; i++) {
            sb.append(fillString(COLUMN_WIDTH + 2, '-'));
            sb.append("+");
        }
    }

    public static void normalizeFitnesses(double[] fitnesses) {
        double min = fitnesses[0];
        double max = fitnesses[0];

        int len = fitnesses.length;
        for (int i = 1; i < len; i++) {
            double fitness = fitnesses[i];
            min = Math.min(min, fitness);
            max = Math.max(max, fitness);
        }

        if (max / min >= 2) {
            return;
        }

        int c = 2; // max probability = c * min probability
        double k = 2 * (c * min - max) / len / (c + 1);

        int idx = len / 2;
        for (int i = 0; i < len; i++) {
            fitnesses[i] += idx * k;

            idx--;
            if (idx == 0 && len % 2 == 0) {
                idx--;
            }
        }
    }
}
