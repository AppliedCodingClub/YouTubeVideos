package com.appliedcoding.video3;

import java.util.Scanner;

/*
Today we are reversing an integer.
 */
public class Problem3 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number: ");
        int n = input.nextInt();
        int reverse = 0;

        while (n != 0) {
            reverse *= 10;
            reverse += n % 10;
            n /= 10;
        }

        System.out.println(reverse);
    }
}