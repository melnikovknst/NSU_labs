package minesweeper.model;

public class Cell {
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private boolean incorrectFlag;
    private int surroundingMines;


    public Cell() {
        this.isMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.incorrectFlag = false;
        this.surroundingMines = 0;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine() {
        isMine = true;
        this.surroundingMines = 1;
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

    public boolean isIncorrectFlag() {
        return incorrectFlag;
    }

    public void setIncorrectFlag(boolean incorrectFlag) {
        this.incorrectFlag = incorrectFlag;
    }

    public int getSurroundingMines() {
        return surroundingMines;
    }

    public void setSurroundingMines(int count) {
        this.surroundingMines = count;
    }
}