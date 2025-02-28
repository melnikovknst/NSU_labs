package minesweeper.controller;

import minesweeper.model.Minefield;
import minesweeper.model.Cell;

public class GameController {
    private final Minefield minefield;
    private boolean gameOver;
    private boolean firstMove;

    public GameController(Minefield minefield) {
        this.minefield = minefield;
        this.gameOver = false;
        this.firstMove = true;
    }

    public boolean revealCell(int row, int col) {
        if (gameOver) {
            return false;
        }

        if (firstMove) {
            minefield.generateMines(row, col);
            firstMove = false;
        }

        Cell cell = minefield.getCell(row, col);

        if (cell.isFlagged()) {
            return true;
        }

        if (cell.isRevealed() && minefield.countFlaggedNeighbors(row, col) == cell.getSurroundingMines()) {
            floodFill(row, col);
            return true;
        }

        cell.reveal();

        if (cell.isMine()) {
            markIncorrectFlags();
            gameOver = true;
            return false;
        }

        if (cell.getSurroundingMines() == 0) {
            floodFill(row, col);
        }

        return true;
    }

    private void floodFill(int row, int col) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int nr = row + dx[i];
            int nc = col + dy[i];

            if (nr >= 0 && nr < minefield.getRows() && nc >= 0 && nc < minefield.getCols()) {
                Cell neighbor = minefield.getCell(nr, nc);

                if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                    neighbor.reveal();
                    if (neighbor.isMine()) {
                        markIncorrectFlags();
                        gameOver = true;
                        return;
                    }

                    if (neighbor.getSurroundingMines() == 0) {
                        floodFill(nr, nc);
                    }
                }
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void toggleFlag(int row, int col) {
        if (gameOver) return;

        Cell cell = minefield.getCell(row, col);
        if (!cell.isRevealed()) {
            cell.toggleFlag();
        }
    }

    private void markIncorrectFlags() {
        for (int r = 0; r < minefield.getRows(); r++) {
            for (int c = 0; c < minefield.getCols(); c++) {
                Cell cell = minefield.getCell(r, c);
                if (cell.isFlagged() && !cell.isMine()) {
                    cell.setIncorrectFlag(true); // Новый флаг для отображения "X"
                }
            }
        }
    }
}