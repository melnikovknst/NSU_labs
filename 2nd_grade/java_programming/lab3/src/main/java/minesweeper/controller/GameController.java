package minesweeper.controller;

import minesweeper.model.Minefield;
import minesweeper.model.GameTimer;
import minesweeper.exceptions.InvalidInputException;

public class GameController {
    private final Minefield minefield;
    private final GameTimer timer;
    private boolean timerStarted;

    public GameController(Minefield minefield) {
        this.minefield = minefield;
        this.timer = new GameTimer();
        this.timerStarted = false;
    }

    public boolean revealCell(int row, int col) throws InvalidInputException {
        if (!timerStarted) {
            timer.startTimer();
            timerStarted = true;
        }

        boolean result = minefield.revealCell(row, col);
        if (minefield.isGameOver() || minefield.isGameWon()) {
            timer.stopTimer();
        }
        return result;
    }

    public int getElapsedTime() {
        return timer.getElapsedTime();
    }

    public Minefield getMinefield() {
        return minefield;
    }
}
