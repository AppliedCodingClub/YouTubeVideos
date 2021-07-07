package com.appliedcoding.video1;

/*
    Read 4 integers from the keyboard and print them out in the 4 corners of the screen.

    1. Read 4 integers
    2. Print integer 1 to the top-left corner (0, 0)
    3. Print integer 2 to the top-right corner (0, 79)
    4. Print integer 3 to the bottom-left corner (23, 0)
    5. Print integer 4 to the bottom-right corner (23, 79)
 */

import java.util.Scanner;

public class FourCorners {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input integer 1: ");
        int int1 = scanner.nextInt();
        System.out.print("Input integer 2: ");
        int int2 = scanner.nextInt();
        System.out.print("Input integer 3: ");
        int int3 = scanner.nextInt();
        System.out.print("Input integer 4: ");
        int int4 = scanner.nextInt();
        System.out.println();

        Console console = new Console();
        console.putStringAt("" + int1, 0, 0);

        String int2AsString = Integer.toString(int2); // "" + int2
        console.putStringAt(int2AsString, 0, 80 - int2AsString.length());

        console.putStringAt("" + int3, 23, 0);

        String int4AsString = Integer.toString(int4);
        console.putStringAt(int4AsString, 23, 80 - int4AsString.length());

        console.printScreen();
    }
}
