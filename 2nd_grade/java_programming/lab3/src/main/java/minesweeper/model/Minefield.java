package minesweeper.model;

import minesweeper.exceptions.InvalidInputException;

import java.util.Random;

public class Minefield {
    private final int rows;
    private final int cols;
    private final int mines;
    private final Cell[][] grid;
    private boolean firstMove;
    private boolean gameOver;
    private boolean gameWon;
    private int flaggedCells;
    private boolean[][] mineMatrix;

    public Minefield(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.grid = new Cell[rows][cols];
        this.firstMove = true;
        this.gameOver = false;
        this.gameWon = false;
        this.flaggedCells = 0;
        this.mineMatrix = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    public void generateMines(int excludeRow, int excludeCol) {
        Random random = new Random();
        int placedMines = 0;

        while (placedMines < mines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            if (!mineMatrix[r][c] && (r != excludeRow || c != excludeCol)) {
                mineMatrix[r][c] = true;
                placedMines++;
            }
        }

        updateSurroundingMines();
    }

    private void updateSurroundingMines() {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isMine()) continue;

                int mineCount = 0;
                for (int i = 0; i < 8; i++) {
                    int nr = r + dx[i];
                    int nc = c + dy[i];

                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc].isMine()) {
                        mineCount++;
                    }
                }
                grid[r][c].setSurroundingMines(mineCount);
            }
        }
    }

    public boolean revealCell(int row, int col) throws InvalidInputException {
        validateCoordinates(row, col);

        if (gameOver || gameWon) {
            return false;
        }

        if (firstMove) {
            generateMines(row, col);
            firstMove = false;
        }

        Cell cell = grid[row][col];

        if (cell.isFlagged()) {
            return true;
        }

        if (cell.isMine()) {
            gameOver = true;
            cell.reveal();
            markIncorrectFlags();
            return false;
        }

        if (cell.isRevealed() && countFlaggedNeighbors(row, col) == cell.getSurroundingMines()) {
            floodFill(row, col);
        } else {
            cell.reveal();
            if (cell.getSurroundingMines() == 0) {
                floodFill(row, col);
            }
        }

        updateGameState();
        return true;
    }

    public void toggleFlag(int row, int col) throws InvalidInputException {
        validateCoordinates(row, col);

        if (gameOver || gameWon) return;
        Cell cell = grid[row][col];

        if (!cell.isRevealed()) {
            cell.toggleFlag();
            updateGameState();
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int getFlaggedCells() {
        return flaggedCells;
    }

    private void updateGameState() {
        flaggedCells = 0;
        boolean allSafeCellsRevealed = true;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];

                if (cell.isFlagged()) {
                    flaggedCells++;
                }

                if (!cell.isMine() && !cell.isRevealed()) {
                    allSafeCellsRevealed = false;
                }
            }
        }

        if (allSafeCellsRevealed) {
            gameWon = true;
        }
    }

    private void floodFill(int row, int col) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int nr = row + dx[i];
            int nc = col + dy[i];

            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                Cell neighbor = grid[nr][nc];

                if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                    neighbor.reveal();

                    if (neighbor.isMine()) {
                        gameOver = true;
                        markIncorrectFlags();
                        return;
                    }

                    if (neighbor.getSurroundingMines() == 0) {
                        floodFill(nr, nc);
                    }
                }
            }
        }

        updateGameState();
    }

    private void markIncorrectFlags() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                if (cell.isFlagged() && !cell.isMine()) {
                    cell.setIncorrectFlag(true);
                }
            }
        }
    }

    public int countFlaggedNeighbors(int row, int col) {
        int count = 0;
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int nr = row + dx[i];
            int nc = col + dy[i];

            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                if (grid[nr][nc].isFlagged()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getTotalMines() {
        return mines;
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    private void validateCoordinates(int row, int col) throws InvalidInputException {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new InvalidInputException("Invalid coordinates. Enter values within the grid range: "
                    + (rows - 1) + " " + (cols - 1) + ".");
        }
    }
}
