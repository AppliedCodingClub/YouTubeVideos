package com.appliedcoding.io;

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
    private boolean[][] clipQuadrants = new boolean[][]{
            {false, false, false, false, true, true, false, true, true}, // 0
            {false, false, false, true, true, true, true, true, true}, // 1
            {false, false, false, true, true, false, true, true, false}, // 2
            {false, true, true, false, true, true, false, true, true}, // 3
            {true, true, true, false, true, true, true, true, true}, // 4
            {true, true, false, true, true, false, true, true, false}, // 5
            {false, true, true, false, true, true, false, false, false}, // 6
            {true, true, true, true, true, true, false, false, false}, // 7
            {true, true, false, true, true, false, false, false, false} // 8
    };

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

    public void line(float x1, float y1, float x2, float y2) {
        if (x1 < 0 && x2 < 0 || x1 >= width && x2 >= width ||
                y1 < 0 && y2 < 0 || y1 >= height && y2 >= height) {
            return; // line does not intersect canvas
        }

        float distX = x2 - x1;
        float distY = y2 - y1;

        float steps = Math.max(Math.abs(distX), Math.abs(distY));
        float stepX = steps == 0 ? 0 : distX / steps;
        float stepY = steps == 0 ? 0 : distY / steps;

        float x = x1;
        float y = y1;

        boolean startInCanvas = x1 >= 0 && x1 < width && y1 >= 0 && y1 < height;
        boolean endInCanvas = x2 >= 0 && x2 < width && y2 >= 0 && y2 < height;

        //calculate clipping points
        // x = x1 + i * stepX
        // y = y1 + i * stepY
        //
        // if x=0 then
        //     i = -x1/stepX
        //     yP = y1 + (-x1/stepX) * stepY = y1 - x1 * stepY / stepX = y1 - x1 * distY / distX
        //
        // if x=width-1 then
        //     i = (width - x1 - 1) / stepX
        //     yP = y1 + (width - x1 - 1) * stepY / stepX = y1 + (width - x1 - 1) * distY / distX
        //
        // if y=0 then
        //     i = -y1/stepY
        //     xP = x1 - y1 * stepX / stepY = x1 - y1 * distX / distY
        //
        // if y=height-1 then
        //     i = (height - 1 - y1) / stepY
        //     xP = x1 + (height - y1 - 1) * stepX / stepY = x1 + (height - y1 - 1) * distX / distY
        //
        // 0 <= xP <= width -1
        // 0 <= yP <= height - 1

        if (startInCanvas && endInCanvas) { // both start and end IN
            // line is fully in viewport. no clipping needed
        } else {
            float xTop = x1 - y1 * distX / distY; // when y=0
            float xBottom = x1 + (height - y1 - 1) * distX / distY; // when y=height-1
            float yLeft = y1 - x1 * distY / distX; // when x=0
            float yRight = y1 + (width - x1 - 1) * distY / distX; // when x=width-1

            if (startInCanvas) { // start IN, end OUT
                if (xTop >= 0 && xTop < width && stepY < 0) {
                    x2 = xTop;
                    y2 = 0;
                } else if (xBottom >= 0 && xBottom < width && stepY > 0) {
                    x2 = xBottom;
                    y2 = height - 1;
                } else if (yLeft >= 0 && yLeft < height && stepX < 0) {
                    x2 = 0;
                    y2 = yLeft;
                } else if (yRight >= 0 && yRight < height && stepX > 0) {
                    x2 = width - 1;
                    y2 = yRight;
                }
            } else if (endInCanvas) { // end IN, start OUT
                if (xTop >= 0 && xTop < width && stepY > 0) {
                    x = xTop;
                    y = 0;
                } else if (xBottom >= 0 && xBottom < width && stepY < 0) {
                    x = xBottom;
                    y = height - 1;
                } else if (yLeft >= 0 && yLeft < height && stepX > 0) {
                    x = 0;
                    y = yLeft;
                } else if (yRight >= 0 && yRight < height && stepX < 0) {
                    x = width - 1;
                    y = yRight;
                }
            } else { // both start and end OUT
                boolean draw = false;
                if (xTop >= 0 && xTop < width) {
                    draw = true;
                    if (stepY > 0) {
                        x = xTop;
                        y = 0;
                    } else {
                        x2 = xTop;
                        y2 = 0;
                    }
                }

                if (xBottom >= 0 && xBottom < width) {
                    draw = true;
                    if (stepY < 0) {
                        x = xBottom;
                        y = height - 1;
                    } else {
                        x2 = xBottom;
                        y2 = height - 1;
                    }
                }

                if (yLeft >= 0 && yLeft < height) {
                    draw = true;
                    if (stepX > 0) {
                        x = 0;
                        y = yLeft;
                    } else {
                        x2 = 0;
                        y2 = yLeft;
                    }
                }

                if (yRight >= 0 && yRight < height) {
                    draw = true;
                    if (stepX < 0) {
                        x = width - 1;
                        y = yRight;
                    } else {
                        x2 = width - 1;
                        y2 = yRight;
                    }
                }

                if (!draw) {
                    return;
                }
            }

            distX = x2 - x;
            distY = y2 - y;
            steps = Math.max(Math.abs(distX), Math.abs(distY));
        }

        int i = 0;
        steps = Math.round(steps);

        do {
            plot(x, y);
            x += stepX;
            y += stepY;
        } while (i++ < steps);
    }

    public void ellipse(float xc, float yc, float rx, float ry) {
        int n = 100;
        for (int i = 0; i < n; i++) {
            double t = Math.PI * 2 / n * i;
            int px = (int) Math.round(xc + rx * Math.cos(t));
            int py = (int) Math.round(yc - ry * Math.sin(t));
            plot(px, py);
        }
    }

    public void midptellipse(float xc, float yc, float rx, float ry) {
        float dx, dy, d1, d2, x = 0, y = ry;
        d1 = ry * ry - rx * rx * ry + 0.25f * rx * rx;
        dx = 2 * ry * ry * x;
        dy = 2 * rx * rx * y;

        while (dx < dy) {
//            plot((int) (xc + x), (int) (yc + y));
//            plot((int) (xc - x), (int) (yc + y));
//            plot((int) (xc + x), (int) (yc - y));
//            plot((int) (xc - x), (int) (yc - y));
            plot(xc + x, yc + y);
            plot(xc - x, yc + y);
            plot(xc + x, yc - y);
            plot(xc - x, yc - y);

            if (fillColor >= 0) {
                setCurrentColor(fillColor);
                line(xc - x, yc - y + 1, xc - x, yc + y - 1);
                line(xc + x, yc - y + 1, xc + x, yc + y - 1);
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
//            plot((int) (xc + x), (int) (yc + y));
//            plot((int) (xc - x), (int) (yc + y));
//            plot((int) (xc + x), (int) (yc - y));
//            plot((int) (xc - x), (int) (yc - y));
            plot(xc + x, yc + y);
            plot(xc - x, yc + y);
            plot(xc + x, yc - y);
            plot(xc - x, yc - y);

            if (fillColor >= 0 && y > 0.5) {
                setCurrentColor(fillColor);
                line(xc - x, yc - y + 1, xc - x, yc + y - 1);
                line(xc + x, yc - y + 1, xc + x, yc + y - 1);
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

    public Console getConsole() {
        return console;
    }
}
