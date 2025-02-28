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
        validateCoordinates(row, col);

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

    public void toggleFlag(int row, int col) throws InvalidInputException {
        validateCoordinates(row, col);
        minefield.toggleFlag(row, col);
    }

    public boolean isGameOver() {
        return minefield.isGameOver();
    }

    public boolean isGameWon() {
        return minefield.isGameWon();
    }

    public int getElapsedTime() {
        return timer.getElapsedTime();
    }

    public Minefield getMinefield() {
        return minefield;
    }

    private void validateCoordinates(int row, int col) throws InvalidInputException {
        if (row < 0 || row >= minefield.getRows() || col < 0 || col >= minefield.getCols()) {
            throw new InvalidInputException("Invalid coordinates. Enter values within the grid range: "
                    + (minefield.getRows() - 1) + " " + (minefield.getCols() - 1) + ".");
        }
    }
}
