package minesweeper.view;

import minesweeper.controller.GameController;
import minesweeper.model.Cell;
import minesweeper.model.Minefield;

import java.util.Scanner;

public class ConsoleView {
    private final GameController controller;
    private final Scanner scanner;

    public ConsoleView(GameController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Minesweeper started!");
        printMinefield();

        while (!controller.isGameOver()) {
            System.out.println("\nEnter command ('row col' to reveal, 'flag row col' to place a flag, 'about' for help, 'exit' to quit):");

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
                    controller.toggleFlag(row, col);
                    printMinefield();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Use: flag <row> <col>");
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
                }
                continue;
            }

            System.out.println("Invalid command. Type 'about' for more information.");
        }

        System.out.println("Game over!");
    }

    private void printAbout() {
        System.out.println("\nAvailable commands:");
        System.out.println("- 'row col' -> Reveal a cell.");
        System.out.println("- 'flag row col' -> Place or remove a flag.");
        System.out.println("- 'about' -> Show available commands.");
        System.out.println("- 'exit' -> Return to the main menu.");
    }

    private void printMinefield() {
        Minefield minefield = controller.getMinefield();

        for (int r = 0; r < minefield.getRows(); r++) {
            for (int c = 0; c < minefield.getCols(); c++) {
                Cell cell = minefield.getCell(r, c);

                if (cell.isIncorrectFlag()) {
                    System.out.print("X ");
                } else if (cell.isFlagged()) {
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
}
