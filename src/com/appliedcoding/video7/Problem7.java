package com.appliedcoding.video7;

import java.util.Scanner;

/*
Tell time in English
For example: 22:20 --> "20 past 10"
*/
public class Problem7 {

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

        if (m > 30 && h == 12) {
            h = 0;
        }

        if (m == 0) {
            System.out.println(h + " o'clock");
        } else if (m < 15) {
            System.out.println(m + " past " + h);
        } else if (m == 15) {
            System.out.println("Quarter past " + h);
        } else if (m < 30) {
            System.out.println(m + " past " + h);
        } else if (m == 30) {
            System.out.println("Half past " + h);
        } else if (m < 45) {
            System.out.println(60 - m + " to " + (h + 1));
        } else if (m == 45) {
            System.out.println("Quarter to " + (h + 1));
        } else {
            System.out.println(60 - m + " to " + (h + 1));
        }
    }
}
