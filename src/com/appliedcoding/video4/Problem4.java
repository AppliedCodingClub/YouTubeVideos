package com.appliedcoding.video4;

import java.util.Scanner;

/*
Today we are converting a String to an integer
 */
public class Problem4 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the String: ");
        String n = input.next();

        int number = 0;
        int j = 0;
        boolean isNegative = false;

        if (n.charAt(0) == '-') {
            j = 1;
            isNegative = true;
        }

        for (int i = j; i < n.length(); i++) {
            int digit = Character.getNumericValue(n.charAt(i));
            number = number * 10 + digit;
//            number *= 10;
//            number += digit;
        }

        if (isNegative) {
            number *= -1;
        }

        System.out.println(number);
    }
}