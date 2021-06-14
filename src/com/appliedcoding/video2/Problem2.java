package com.appliedcoding.video2;

import java.util.Arrays;
import java.util.Scanner;

public class Problem2 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the limit: ");
        int n = input.nextInt();

        sieveEratosthenes(n);
    }

    public static void sieveEratosthenes(int n) {
        boolean[] numbers = new boolean[n + 1];

        for (int i = 2; i < n / 2; i++) {
            if (!numbers[i]) {
                for (int j = i * 2; j < n; j += i) {
                    numbers[j] = true;
                }
            }
        }

        System.out.println(Arrays.toString(numbers));
    }
}