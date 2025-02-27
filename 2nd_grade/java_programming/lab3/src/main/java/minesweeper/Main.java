package minesweeper;

import minesweeper.controller.GameController;
import minesweeper.model.Minefield;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Minesweeper started!");

        Minefield minefield = new Minefield(9, 9, 10);
        GameController controller = new GameController(minefield);

        controller.printMinefield();

        Scanner scanner = new Scanner(System.in);
        while (!controller.isGameOver()) {
            System.out.println("\nEnter command ('row col' to reveal, 'flag row col' to place a flag, 'exit' to quit):");

            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the game...");
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length == 3 && parts[0].equalsIgnoreCase("flag")) {
                try {
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);

                    controller.toggleFlag(row, col);
                    controller.printMinefield();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter: flag row col");
                }
                continue;
            }

            if (parts.length != 2) {
                System.out.println("Invalid input. Please enter 'row col' or 'flag row col'.");
                continue;
            }

            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);

                controller.revealCell(row, col);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter two numbers separated by a space.");
            }
        }

        scanner.close();
        System.out.println("Game over!");
    }
}