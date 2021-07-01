package com.appliedcoding.io;

import com.appliedcoding.snakegame.model.Position;

import java.util.Arrays;

/*
    Canvas coordinates: 0,0 - top left ===>>> width-1, height-1 - bottom right
    width > 0 and height > 0 always
 */
public class CanvasBase {

    private static final String COLOR_BK_PREFIX = "\u001B[48;5;";
    private static final String COLOR_FG_PREFIX = "\u001B[38;5;";
    private static final String COLOR_SUFFIX = "m";

    private final Console console;
    private final Position topLeft;
    private int[][] buffer;
    private Position bottomRight;
    private int width;
    private int height;
    private int backgroundColor;
    private int color;
    private int currentColor;
    private int fillColor = -1;

    // topLeft, bottomRight - real screen coordinates
    public CanvasBase(Console console, Position topLeft, Position bottomRight) {
        this.console = console;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        width = bottomRight.getX() - topLeft.getX() + 1;
        height = bottomRight.getY() - topLeft.getY() + 1;
        if (height % 2 == 1) {
            throw new IllegalArgumentException("Height must be an even number");
        }
        buffer = new int[width][height];
    }

    // topLeft - real screen coordinates; width, height - canvas dimensions
    public CanvasBase(Console console, Position topLeft, int width, int height) {
        if (height % 2 == 1) {
            throw new IllegalArgumentException("Height must be an even number");
        }
        this.console = console;
        this.topLeft = topLeft;
        this.width = width;
        this.height = height;
        bottomRight = new Position(topLeft.getX() + width, topLeft.getY() + height);
        buffer = new int[width][height];
    }

    public void plot(float x, float y) {
        int xx = Math.round(x);
        int yy = Math.round(y);

        if (xx < 0 || yy < 0 || xx >= width || yy >= height) {
            return;
        }

        String s;
        if (yy % 2 == 0) { // even line --> upper
            int colorBelow = buffer[xx][yy + 1];
            if (currentColor == colorBelow) { // is obstacle below?
                s = "\u2588"; // full block █
            } else {
                setBackgroundColor(colorBelow);
                s = "\u2580"; // upper half ▀
            }
        } else { // odd line --> lower
            int colorAbove = buffer[xx][yy - 1];
            if (currentColor == colorAbove) { // is obstacle above?
                s = "\u2588"; // full block █
            } else {
                setBackgroundColor(colorAbove);
                s = "\u2584"; // lower half ▄
            }
        }

        buffer[xx][yy] = currentColor;
        console.printAt(s, toConsolePosition(xx, yy));
    }

    public void line(float startX, float startY, float endX, float endY) {
        float distX = endX - startX;
        float distY = endY - startY;

        float steps = Math.max(Math.abs(distX), Math.abs(distY));
        float stepX = distX / steps;
        float stepY = distY / steps;

        float x = startX;
        float y = startY;

        for (float i = 0; i <= steps; i++) {
            plot(x, y);

            x += stepX;
            y += stepY;
        }
    }

    public void ellipse(float xc, float yc, float rx, float ry) {
        int n = 100;
        for (int i = 0; i < n; i++) {
            double t = Math.PI * 2 / n * i;
            int px = (int) Math.round(xc + rx * Math.cos(t));
            int py = (int) Math.round(yc - ry * Math.sin(t));
            setColor((int) (1 + Math.random() * 254));
            plot(px, py);
        }
    }

    public void midptellipse(float xc, float yc, float rx, float ry) {
        float dx, dy, d1, d2, x = 0, y = ry;
        d1 = ry * ry - rx * rx * ry + 0.25f * rx * rx;
        dx = 2 * ry * ry * x;
        dy = 2 * rx * rx * y;

        while (dx < dy) {
            plot((int) (xc + x), (int) (yc + y));
            plot((int) (xc - x), (int) (yc + y));
            plot((int) (xc + x), (int) (yc - y));
            plot((int) (xc - x), (int) (yc - y));

            if (fillColor >= 0) {
                setCurrentColor(fillColor);
                line((int) (xc - x), (int) (yc - y + 1), (int) (xc - x), (int) (yc + y - 1));
                line((int) (xc + x), (int) (yc - y + 1), (int) (xc + x), (int) (yc + y - 1));
                setCurrentColor(color);
            }

            if (d1 < 0) {
                x++;
                dx += 2 * ry * ry;
                d1 += dx + ry * ry;
            } else {
                x++;
                y--;
                dx += 2 * ry * ry;
                dy -= 2 * rx * rx;
                d1 += dx - dy + ry * ry;
            }
        }

        d2 = ry * ry * (x + 0.5f) * (x + 0.5f) + rx * rx * (y - 1) * (y - 1) - rx * rx * ry * ry;

        while (y >= 0) {
            plot((int) (xc + x), (int) (yc + y));
            plot((int) (xc - x), (int) (yc + y));
            plot((int) (xc + x), (int) (yc - y));
            plot((int) (xc - x), (int) (yc - y));

            if (fillColor >= 0 && y > 0) {
                setCurrentColor(fillColor);
                line((int) (xc - x), (int) (yc - y + 1), (int) (xc - x), (int) (yc + y - 1));
                line((int) (xc + x), (int) (yc - y + 1), (int) (xc + x), (int) (yc + y - 1));
                setCurrentColor(color);
            }

            if (d2 > 0) {
                y--;
                dy -= 2 * rx * rx;
                d2 += rx * rx - dy;
            } else {
                y--;
                x++;
                dx += 2 * ry * ry;
                dy -= 2 * rx * rx;
                d2 += dx - dy + rx * rx;
            }
        }
    }

    public void rectangle(float x, float y, float width, float height) {
        int x1 = Math.round(x);
        int y1 = Math.round(y);
        int x2 = Math.round(x + width - 1);
        int y2 = Math.round(y + height - 1);

        if (x1 > x2) {
            int aux = x1;
            x1 = x2;
            x2 = aux;
        }

        if (y1 > y2) {
            int aux = y1;
            y1 = y2;
            y2 = aux;
        }

        line(x1, y1, x2, y1);
        line(x1, y1, x1, y2);
        line(x2, y1, x2, y2);
        line(x1, y2, x2, y2);

        if (fillColor >= 0 && x2 - x1 > 1 && y2 - y1 > 1) {
            setCurrentColor(fillColor);
            for (int yy = y1 + 1; yy < y2; yy++) {
                line(x1 + 1, yy, x2 - 1, yy);
            }
            setCurrentColor(color);
        }
    }

    public void resize(int amount) {
        bottomRight = new Position(bottomRight.getX() + 2 * amount, bottomRight.getY() + amount);
        width = bottomRight.getX() - topLeft.getX() + 1;
        height = bottomRight.getY() - topLeft.getY() + 1;
        buffer = new int[width][height];
        clear();
    }

    public void clear() {
        char[] chars = new char[width];
        Arrays.fill(chars, ' ');
        String line = new String(chars);
        for (int y = 0; y < height; y++) {
            console.printAt(line, toConsolePosition(0, y));
            for (int x = 0; x < width; x++) {
                buffer[x][y] = backgroundColor;
            }
        }
    }

    public void printAt(String message, int x, int y) {
        console.printAt(message, toConsolePosition(x, y));
    }

    public void printMessage(String message) {
        console.setBackgroundColor(Console.ANSI_YELLOW_BACKGROUND);
        console.setTextColor(Console.ANSI_RED);
        int x = topLeft.getX() + (bottomRight.getX() - topLeft.getX()) / 2;
        int y = Math.max(2, topLeft.getY() + (bottomRight.getY() - topLeft.getY()) / 8);
        console.printAt(message, x - message.length() / 2, y);
    }

    protected Position toConsolePosition(int x, int y) {
        return new Position(x + topLeft.getX(), topLeft.getY() + y / 2);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setColor(int ansi8BitColor) {
        color = ansi8BitColor;
        setCurrentColor(color);
    }

    public void setBackgroundColor(int ansi8BitColor) {
        backgroundColor = ansi8BitColor;
        console.setBackgroundColor(COLOR_BK_PREFIX + ansi8BitColor + COLOR_SUFFIX);
    }

    public void setFillColor(int ansi8BitColor) {
        fillColor = ansi8BitColor;
    }

    public void noFill() {
        fillColor = -1;
    }

    public void setCurrentColor(int ansi8BitColor) {
        currentColor = ansi8BitColor;
        setConsoleColor(ansi8BitColor);
    }

    private void setConsoleColor(int ansi8BitColor) {
        console.setTextColor(COLOR_FG_PREFIX + ansi8BitColor + COLOR_SUFFIX);
    }
}
