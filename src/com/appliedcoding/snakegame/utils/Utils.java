package com.appliedcoding.snakegame.utils;

import com.appliedcoding.snakegame.exception.SnakeException;
import com.appliedcoding.snakegame.io.Console;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static int PAUSE = 10;

    public static <T> List<List<T>> partition(List<T> list, int numberOfPartitions) {
        List<List<T>> result = new ArrayList<>(numberOfPartitions);
        for (int i = 0; i < numberOfPartitions; i++) {
            result.add(new ArrayList<>());
        }

        int i = 0;
        for (T element : list) {
            result.get(i % numberOfPartitions).add(element);
            i++;
        }

        return result;
    }

    public static void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    public static Console setupConsole() {
        Console console = new Console();
        console.hideCursor();
        console.enterCharacterMode();
        console.setTextColor(Console.ANSI_RESET);
        console.clear();

        return console;
    }

    public static <T> void serialize(T payload, String file) throws SnakeException {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(payload);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            throw new SnakeException(e);
        }
    }

    public static <T> T deserialize(String file) throws SnakeException {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            T result = (T) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

            return result;
        } catch (ClassNotFoundException | IOException e) {
            throw new SnakeException(e);
        }
    }
}
