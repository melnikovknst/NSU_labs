package minesweeper.controller;

import minesweeper.model.Minefield;
import minesweeper.model.Cell;

public class GameController {
    private final Minefield minefield;
    private boolean gameOver;

    public GameController(Minefield minefield) {
        this.minefield = minefield;
        this.gameOver = false;
    }

    public void revealCell(int row, int col) {
        if (gameOver) {
            System.out.println("Game is already over.");
            return;
        }

        if (row < 0 || row >= minefield.getRows() || col < 0 || col >= minefield.getCols()) {
            System.out.println("Coordinates out of bounds. Try again.");
            return;
        }

        minefield.revealCell(row, col);
        printMinefield();

        if (minefield.getCell(row, col).isMine()) {
            System.out.println("You hit a mine!");
            gameOver = true;
        } else if (checkWin()) {
            System.out.println("Congratulations! You cleared the minefield.");
            gameOver = true;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private boolean checkWin() {
        for (int r = 0; r < minefield.getRows(); r++) {
            for (int c = 0; c < minefield.getCols(); c++) {
                Cell cell = minefield.getCell(r, c);
                if (!cell.isMine() && !cell.isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void toggleFlag(int row, int col) {
        Cell cell = minefield.getCell(row, col);

        if (cell.isRevealed()) {
            System.out.println("Cannot place a flag on an opened cell.");
            return;
        }

        cell.toggleFlag();
    }

    public void printMinefield() {
        for (int r = 0; r < minefield.getRows(); r++) {
            for (int c = 0; c < minefield.getCols(); c++) {
                Cell cell = minefield.getCell(r, c);

                if (!cell.isRevealed()) {
                    System.out.print("■ ");
                } else if (cell.isMine()) {
                    System.out.print("* ");
                } else if (cell.isFlagged()) {
                    System.out.print("P ");
                }else {
                    System.out.print(cell.getSurroundingMines() + " ");
                }
            }
            System.out.println();
        }
    }
}