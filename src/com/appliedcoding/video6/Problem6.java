package com.appliedcoding.video6;

import java.util.Scanner;

/*
Tell the time in Dutch: "22:20" --> 10 voor half 11
 */
public class Problem6 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the time (in the format of hh:mm): ");
        String n = input.next();
        String[] parts = n.split(":");
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        tellTime(h, m);
    }

    public static void tellTime(int h, int m) {
        if (h > 12) {
            h -= 12;
        }

        if (m > 15 && h == 12) {
            h = 0;
        }

        if (m == 0) {
            System.out.println(h + " uur");
        } else if (m < 15) {
            System.out.println(m + " over " + h);
        } else if (m == 15) {
            System.out.println("Kwart over " + h);
        } else if (m < 30) {
            System.out.println(30 - m + " voor half " + (h + 1));
        } else if (m == 30) {
            System.out.println("Half " + (h + 1));
        } else if (m < 45) {
            System.out.println(m - 30 + " over half " + (h + 1));
        } else if (m == 45) {
            System.out.println("Kwart voor " + (h + 1));
        } else {
            System.out.println(60 - m + " voor " + (h + 1));
        }
    }
}