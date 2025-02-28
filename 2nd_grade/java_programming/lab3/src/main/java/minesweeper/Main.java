package minesweeper;

import minesweeper.controller.GameController;
import minesweeper.model.Minefield;
import minesweeper.view.ConsoleView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Minesweeper! Type 'about' for more information.");

        while (true) {
            System.out.println("\nEnter command:");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("exit")) {
                System.out.println("Exiting the game...");
                break;
            }

            if (input.equals("about")) {
                printAbout();
                continue;
            }

            if (input.startsWith("new game")) {
                String[] parts = input.split(" ");
                if (parts.length != 6) {
                    System.out.println("Invalid command. Correct format: new game <text/graphic> <rows> <cols> <mines>");
                    continue;
                }

                String mode = parts[2];
                int rows, cols, mines;
                try {
                    rows = Integer.parseInt(parts[3]);
                    cols = Integer.parseInt(parts[4]);
                    mines = Integer.parseInt(parts[5]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid command. Rows, cols, and mines must be integers.");
                    continue;
                }

                if (!mode.equals("text") && !mode.equals("graphic")) {
                    System.out.println("Invalid mode. Use 'text' or 'graphic'.");
                    continue;
                }

                startGame(mode, rows, cols, mines);
                continue;
            }

            System.out.println("Invalid command. Type 'about' for more information.");
        }

        scanner.close();
    }

    private static void printAbout() {
        System.out.println("\nAvailable commands:");
        System.out.println("- 'exit' -> Quit the game.");
        System.out.println("- 'about' -> Show available commands.");
        System.out.println("- 'new game <text/graphic> <rows> <cols> <mines>' -> Start a new game.");
    }

    private static void startGame(String mode, int rows, int cols, int mines) {
        Minefield minefield = new Minefield(rows, cols, mines);
        GameController controller = new GameController(minefield);

        if (mode.equals("text")) {
            ConsoleView view = new ConsoleView(controller);
            view.start();
        } else {
            System.out.println("Graphical mode is not implemented yet.");
        }
    }
}
