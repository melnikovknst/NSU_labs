package minesweeper.controller;

import minesweeper.model.Minefield;

public class GameController {
    private final Minefield minefield;

    public GameController(Minefield minefield) {
        this.minefield = minefield;
    }

    public boolean revealCell(int row, int col) {
        return minefield.revealCell(row, col);
    }

    public void toggleFlag(int row, int col) {
        minefield.toggleFlag(row, col);
    }

    public boolean isGameOver() {
        return minefield.isGameOver();
    }

    public boolean isGameWon() {
        return minefield.isGameWon();
    }

    public Minefield getMinefield() {
        return minefield;
    }
}
