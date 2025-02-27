package minesweeper.model;

import java.util.Random;

public class Minefield {
    private final int rows;
    private final int cols;
    private final int mines;
    private final Cell[][] grid;
    private boolean firstMove;

    public Minefield(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.grid = new Cell[rows][cols];
        this.firstMove = true;

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

            if (!grid[r][c].isMine() && (r != excludeRow || c != excludeCol)) {
                grid[r][c].setMine();
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

    public Cell getCell(int row, int col) {
        return grid[row][col];
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

                    if (neighbor.getSurroundingMines() == 0) {
                        floodFill(nr, nc);
                    }
                }
            }
        }
    }

    public void revealCell(int row, int col) {
        if (firstMove) {
            generateMines(row, col);
            firstMove = false;
        }

        if (grid[row][col].isRevealed() || grid[row][col].isFlagged()) {
            return;
        }

        grid[row][col].reveal();

        if (grid[row][col].getSurroundingMines() == 0) {
            floodFill(row, col);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}