package minesweeper.view;

import minesweeper.controller.GameController;
import minesweeper.model.Cell;
import minesweeper.model.Minefield;
import minesweeper.model.HighScoresManager;
import minesweeper.exceptions.InvalidInputException;

import java.util.Scanner;

public class ConsoleView {
    private final GameController controller;
    private final Scanner scanner;
    private final HighScoresManager scoresManager;
    private final Minefield minefield;

    public ConsoleView(GameController controller, HighScoresManager scoresManager) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
        this.scoresManager = scoresManager;
        this.minefield = controller.getMinefield();
    }

    public void start() {
        System.out.println("Minesweeper started!");
        printMinefield();

        while (!minefield.isGameOver() && !minefield.isGameWon()) {
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Returning to menu...");
                return;
            }

            if (input.equalsIgnoreCase("about")) {
                printAbout();
                continue;
            }

            String[] parts = input.split(" ");
            if (parts.length == 3 && parts[0].equalsIgnoreCase("flag")) {
                int row, col;
                try {
                    row = Integer.parseInt(parts[1]);
                    col = Integer.parseInt(parts[2]);
                    minefield.toggleFlag(row, col);
                    printMinefield();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Use: flag <row> <col>");
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
                continue;
            }

            if (parts.length == 2) {
                int row, col;
                try {
                    row = Integer.parseInt(parts[0]);
                    col = Integer.parseInt(parts[1]);
                    if (!controller.revealCell(row, col)) {
                        System.out.println("You hit a mine!");
                    }
                    printMinefield();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter two numbers separated by a space.");
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
                continue;
            }

            System.out.println("Invalid command. Type 'about' for more information.");
        }

        if (minefield.isGameWon()) {
            int time = controller.getElapsedTime();
            System.out.println("Congratulations! You cleared the minefield in " + time + " seconds!");
            System.out.print("Enter your name: ");
            String playerName = scanner.nextLine().trim();
            scoresManager.addScore(playerName, time);
        } else {
            System.out.println("Game over!");
        }
    }

    private void printAbout() {
        System.out.println("\nAvailable commands:");
        System.out.println("- 'row col' -> Reveal a cell.");
        System.out.println("- 'flag row col' -> Place or remove a flag.");
        System.out.println("- 'about' -> Show available commands.");
        System.out.println("- 'exit' -> Return to the main menu.");
    }

    private void printMinefield() {
        int flagged = minefield.getFlaggedCells();
        int totalMines = minefield.getTotalMines();
        int timeElapsed = controller.getElapsedTime();

        System.out.println("\nmines: " + flagged + "/" + totalMines + " | time: " + timeElapsed + "s");

        for (int r = 0; r < minefield.getRows(); r++) {
            for (int c = 0; c < minefield.getCols(); c++) {
                Cell cell = minefield.getCell(r, c);

                if (cell.isIncorrectFlag()) {
                    System.out.print("X ");
                } else if (cell.isFlagged()) {
                    System.out.print("P ");
                } else if (minefield.isMine(r, c)) {
                    if (!cell.isRevealed()) {
                        System.out.print("o ");
                    } else {
                        System.out.print("* ");
                    }
                } else if (!cell.isRevealed()) {
                    System.out.print("■ ");
                } else {
                    System.out.print(cell.getSurroundingMines() + " ");
                }
            }
            System.out.println();
        }
    }
}
