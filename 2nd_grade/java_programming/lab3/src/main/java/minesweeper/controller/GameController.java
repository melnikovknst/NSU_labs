package minesweeper.controller;

import minesweeper.model.Minefield;
import minesweeper.model.GameTimer;

public class GameController {
    private final Minefield minefield;
    private final GameTimer timer;

    public GameController(Minefield minefield) {
        this.minefield = minefield;
        this.timer = new GameTimer();
        timer.startTimer();
    }

    public boolean revealCell(int row, int col) {
        boolean result = minefield.revealCell(row, col);
        if (minefield.isGameOver() || minefield.isGameWon()) {
            timer.stopTimer();
        }
        return result;
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

    public int getElapsedTime() {
        return timer.getElapsedTime();
    }
}
