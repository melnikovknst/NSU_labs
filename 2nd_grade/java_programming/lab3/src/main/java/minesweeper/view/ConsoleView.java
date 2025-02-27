package minesweeper.view;

import minesweeper.controller.GameController;
import minesweeper.model.Cell;
import minesweeper.model.Minefield;

import java.util.Scanner;

public class ConsoleView {
    private final GameController controller;
    private final Minefield minefield;
    private final Scanner scanner;

    public ConsoleView(GameController controller, Minefield minefield) {
        this.controller = controller;
        this.minefield = minefield;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Minesweeper started!");
        printMinefield();

        while (!controller.isGameOver()) {
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
                processFlagCommand(parts);
                printMinefield();
                continue;
            }

            if (parts.length == 2) {
                processRevealCommand(parts);
                printMinefield();
                continue;
            }

            System.out.println("Invalid command. Type 'about' for more information.");
        }

        System.out.println("Game over!");
    }

    private void processFlagCommand(String[] parts) {
        try {
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            controller.toggleFlag(row, col);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter: flag row col");
        }
    }

    private void processRevealCommand(String[] parts) {
        try {
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            controller.revealCell(row, col);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter two numbers separated by a space.");
        }
    }

    private void printMinefield() {
        for (int r = 0; r < minefield.getRows(); r++) {
            for (int c = 0; c < minefield.getCols(); c++) {
                Cell cell = minefield.getCell(r, c);

                if (cell.isFlagged()) {
                    System.out.print("P ");
                } else if (!cell.isRevealed()) {
                    System.out.print("■ ");
                } else if (cell.isMine()) {
                    System.out.print("* ");
                } else {
                    System.out.print(cell.getSurroundingMines() + " ");
                }
            }
            System.out.println();
        }
    }

    private void printAbout() {
        System.out.println("\nAvailable commands:");
        System.out.println("- 'row col' -> Reveal a cell.");
        System.out.println("- 'flag row col' -> Place or remove a flag.");
        System.out.println("- 'exit' -> Return to main menu.");
        System.out.println("- 'about' -> Show this help message.");
    }
}