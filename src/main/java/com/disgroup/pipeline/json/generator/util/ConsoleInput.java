package com.disgroup.pipeline.json.generator.util;

import java.util.Scanner;
import java.util.function.IntPredicate;

public class ConsoleInput {
    private final Scanner sc = new Scanner(System.in);

    public String askString(String prompt, String defVal) {
        System.out.print(prompt + " [" + defVal + "]: ");
        String line = sc.nextLine();
        return line.trim().isEmpty() ? defVal : line.trim();
    }

    public int askInt(String prompt, int defVal, IntPredicate validator) {
        while (true) {
            try {
                int val = Integer.parseInt(askString(prompt, String.valueOf(defVal)));
                if (validator.test(val)) return val;
            } catch (NumberFormatException ignored) {}
            System.out.println("Некорректное число, повторите.");
        }
    }

    public String nextLine() { return sc.nextLine(); }
}
