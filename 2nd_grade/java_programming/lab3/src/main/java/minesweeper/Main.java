package minesweeper;

import minesweeper.model.Cell;
import minesweeper.model.Minefield;
import minesweeper.model.GameTimer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Minesweeper started!");

        Minefield minefield = new Minefield(9, 9, 10);
        printMinefield(minefield);

        GameTimer timer = new GameTimer();
        timer.startTimer();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nEnter coordinates to reveal (row col) or type 'exit' to quit:");

            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the game...");
                timer.stopTimer();
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length != 2) {
                System.out.println("Invalid input. Please enter two numbers.");
                continue;
            }

            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);

                if (row < 0 || row >= minefield.getRows() || col < 0 || col >= minefield.getCols()) {
                    System.out.println("Coordinates out of bounds. Try again.");
                    continue;
                }

                minefield.revealCell(row, col);
                printMinefield(minefield);

                if (minefield.getCell(row, col).isMine()) {
                    System.out.println("You hit a mine!");
                    timer.stopTimer();
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter two numbers separated by a space.");
            }
        }

        scanner.close();
        System.out.println("Game over! Time elapsed: " + timer.getElapsedTime() + " seconds.");
    }

    private static void printMinefield(Minefield minefield) {
        for (int r = 0; r < minefield.getRows(); r++) {
            for (int c = 0; c < minefield.getCols(); c++) {
                Cell cell = minefield.getCell(r, c);

                if (!cell.isRevealed()) {
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