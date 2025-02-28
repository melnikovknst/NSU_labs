package minesweeper;

import minesweeper.controller.GameController;
import minesweeper.model.Minefield;
import minesweeper.view.ConsoleView;
import minesweeper.model.HighScoresManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HighScoresManager scoresManager = new HighScoresManager();

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

            if (input.equals("high scores")) {
                printHighScores(scoresManager);
                continue;
            }

            if (input.startsWith("new game")) {
                String[] parts = input.split(" ");

                String mode;
                int rows = 9, cols = 9, mines = 10;

                if (parts.length == 3 || parts.length == 6) {
                    mode = parts[2];

                    if (parts.length == 6) {
                        try {
                            rows = Integer.parseInt(parts[3]);
                            cols = Integer.parseInt(parts[4]);
                            mines = Integer.parseInt(parts[5]);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid command. Rows, cols, and mines must be integers.");
                            continue;
                        }
                    }
                } else {
                    System.out.println("Invalid command. Use: new game <text/graphic> [rows] [cols] [mines]");
                    continue;
                }

                if (!mode.equals("text") && !mode.equals("graphic")) {
                    System.out.println("Invalid mode. Use 'text' or 'graphic'.");
                    continue;
                }

                startGame(mode, rows, cols, mines, scoresManager);
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
        System.out.println("- 'high scores' -> Show the best times.");
        System.out.println("- 'new game <text/graphic> [rows] [cols] [mines]' -> Start a new game.");
    }

    private static void printHighScores(HighScoresManager scoresManager) {
        System.out.println("\nHigh Scores:");
        for (String score : scoresManager.getHighScores()) {
            System.out.println(score);
        }
    }

    private static void startGame(String mode, int rows, int cols, int mines, HighScoresManager scoresManager) {
        Minefield minefield = new Minefield(rows, cols, mines);
        GameController controller = new GameController(minefield);

        if (mode.equals("text")) {
            ConsoleView view = new ConsoleView(controller, scoresManager);
            view.start();
        } else {
            System.out.println("Graphical mode is not implemented yet.");
        }
    }
}
