package minesweeper.model;

public class Cell {
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int surroundingMines;

    public Cell() {
        this.isMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.surroundingMines = 0;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine() {
        isMine = true;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void reveal() {
        isRevealed = true;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void toggleFlag() {
        isFlagged = !isFlagged;
    }

    public int getSurroundingMines() {
        return surroundingMines;
    }

    public void setSurroundingMines(int count) {
        this.surroundingMines = count;
    }
}