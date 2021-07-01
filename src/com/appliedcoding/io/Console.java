package com.appliedcoding.io;

import com.appliedcoding.snakegame.model.Position;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* See https://en.wikipedia.org/wiki/ANSI_escape_code */
public class Console {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m"; // ESC [ 31 m
    public static final String ANSI_BRIGHT_RED = "\u001B[91m"; // ESC [ 91 m
    public static final String ANSI_SALMON_RED = "\u001B[38;5;203m"; // ESC [ 38;5;203 m
    public static final String ANSI_SALMON_RED_BACKGROUND = "\u001B[48;5;203m"; // ESC [ 48;5;203 m
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[103m";
    public static final String ANSI_BRIGHT_YELLOW = "\u001B[38;5;226m"; // ESC [ 38;5;226 m
    public static final String ANSI_GREEN = "\u001B[32m"; // ESC [ 32 m
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BRIGHT_WHITE = "\u001B[38;5;15m"; // ESC [ 38;5;15 m
    public static final String ANSI_BRIGHT_WHITE_BACKGROUND = "\u001B[107m"; // ESC [ 107 m

    public Position readCurrentPosition() {
        try {
            System.out.print("\u001B[6n");
            // CSI 6n Device Status Report Reports the cursor position (CPR)
            // by transmitting ESC[n;mR, where n is the row and m is the column.

            String result = "";
            int character;

            do {
                character = System.in.read();
                if (character == 27) {
                    result += "^";
                } else {
                    result += (char) character;
                }
            } while (character != 82); // 'R'

            Pattern pattern = Pattern.compile("\\^\\[(\\d+);(\\d+)R");
            Matcher matcher = pattern.matcher(result);
            if (matcher.matches()) {
                return new Position(Integer.valueOf(matcher.group(2)), Integer.valueOf(matcher.group(1)));
            } else {
                return new Position(1, 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Position(1, 1);
        }
    }

    public void setTextColor(String color) {
        System.out.print(color);
    }

    public void setBackgroundColor(String color) {
        System.out.print(color);
    }

    public Position detectScreenSize() {
        Position initialPosition = readCurrentPosition();
        gotoXY(10000, 10000);
        Position result = readCurrentPosition();
        gotoXY(initialPosition.getX(), initialPosition.getY());

        return result;
    }

    public void clear() {
        System.out.print("\u001B[2J"); // CSI 2 J
    }

    public void gotoXY(Position screenPosition) {
        System.out.print(String.format("\u001B[%d;%dH", screenPosition.getY(), screenPosition.getX())); // CSI n ; m H
    }

    public void gotoXY(int x, int y) {
        System.out.print(String.format("\u001B[%d;%dH", y, x)); // CSI n ; m H
    }

    public void printAt(String message, int x, int y, Position offset) {
        printAt(message, x + offset.getX(), y + offset.getY());
    }

    public void printAt(String message, int x, int y) {
        gotoXY(x, y);
        System.out.print(message);
    }

    public void printAt(String message, Position screenPosition) {
        printAt(message, screenPosition.getX(), screenPosition.getY());
    }

    public void hideCursor() {
        System.out.print("\u001B[?25l"); // CSI ? 25 l
    }

    public void showCursor() {
        System.out.print("\u001B[?25h"); // CSI ? 25 h
    }

    public void enterCharacterMode() {
        try {
            String[] cmd = {"/bin/sh", "-c", "stty raw </dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void enterLineMode() {
        try {
            String[] cmd = {"/bin/sh", "-c", "stty cooked</dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
