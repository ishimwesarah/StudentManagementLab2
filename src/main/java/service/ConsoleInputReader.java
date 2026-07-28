package service;

import exception.InvalidGradeException;

import java.util.Scanner;

public class ConsoleInputReader {

    private final Scanner scanner;

    public ConsoleInputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readMenuChoice() {
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int readNumberBetween(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input.trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public double readGradeBound(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input.trim());
                if (value >= 0 && value <= 100) {
                    return value;
                }
                System.out.println("Please enter a value between 0 and 100.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private double parseGrade() throws InvalidGradeException {
        System.out.print("Enter grade (0-100): ");
        String input = scanner.nextLine();

        double value;
        try {
            value = Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            throw new InvalidGradeException("Grade must be a valid number. You entered: '" + input.trim() + "'");
        }

        if (value < 0 || value > 100) {
            throw new InvalidGradeException("Grade must be between 0 and 100. You entered: " + value);
        }

        return value;
    }

    public double promptForGrade() {
        while (true) {
            try {
                return parseGrade();
            } catch (InvalidGradeException e) {
                System.out.println();
                System.out.println("\u2717 ERROR: InvalidGradeException");
                System.out.println("  " + e.getMessage());
                System.out.println();
                System.out.print("  Try again? (Y/N): ");
                String retry = scanner.nextLine();
                if (!retry.equalsIgnoreCase("Y")) {
                    return -1;
                }
            }
        }
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public void print(String text) {
        System.out.print(text);
    }
}