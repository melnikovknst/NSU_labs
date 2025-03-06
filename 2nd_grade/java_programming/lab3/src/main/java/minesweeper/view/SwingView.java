package minesweeper.view;

import minesweeper.controller.GameController;
import minesweeper.model.Minefield;
import minesweeper.model.Cell;
import minesweeper.exceptions.InvalidInputException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

public class SwingView extends JFrame {
    private final GameController controller;
    private final JButton[][] buttons;
    private final JLabel statusLabel;
    private final int rows;
    private final int cols;

    private final ImageIcon mineIcon;
    private final ImageIcon flagIcon;
    private final ImageIcon wrongFlagIcon;
    private final ImageIcon explosionIcon;

    public SwingView(GameController controller) {
        this.controller = controller;
        Minefield minefield = controller.getMinefield();
        this.rows = minefield.getRows();
        this.cols = minefield.getCols();
        this.buttons = new JButton[rows][cols];

        // Загружаем изображения
        mineIcon = loadIcon("/mine.png");
        flagIcon = loadIcon("/flag.png");
        wrongFlagIcon = loadIcon("/wrong_flag.png");
        explosionIcon = loadIcon("/explosion.png");

        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Панель состояния
        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, BorderLayout.NORTH);

        JPanel gamePanel = new JPanel(new GridLayout(rows, cols));
        add(gamePanel, BorderLayout.CENTER);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                final int row = r;
                final int col = c;

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        try {
                            if (SwingUtilities.isRightMouseButton(e)) {
                                controller.toggleFlag(row, col);
                            } else if (SwingUtilities.isLeftMouseButton(e)) {
                                if (!controller.revealCell(row, col)) {
                                    buttons[row][col].setIcon(resizeIcon(explosionIcon, button));
                                    JOptionPane.showMessageDialog(SwingView.this, "You hit a mine!", "Game Over", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (InvalidInputException err) {
                            System.out.println(err.getMessage());
                        }
                        updateBoard();
                    }
                });

                buttons[r][c] = button;
                gamePanel.add(button);
            }
        }

        updateBoard();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateBoard() {
        Minefield minefield = controller.getMinefield();
        int flagged = minefield.getFlaggedCells();
        int totalMines = minefield.getTotalMines();
        int timeElapsed = controller.getElapsedTime();

        // Обновляем статусную строку
        statusLabel.setText("Mines: " + flagged + "/" + totalMines + " | Time: " + timeElapsed + "s");

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = minefield.getCell(r, c);
                JButton button = buttons[r][c];

                if (cell.isRevealed()) {
                    button.setEnabled(false);
                    if (cell.isMine()) {
                        button.setIcon(resizeIcon(mineIcon, button));
                    } else {
                        int count = cell.getSurroundingMines();
                        button.setText(count > 0 ? String.valueOf(count) : "");
                    }
                } else if (cell.isFlagged()) {
                    if (controller.isGameOver() && !cell.isMine()) {
                        button.setIcon(resizeIcon(wrongFlagIcon, button));
                    } else {
                        button.setIcon(resizeIcon(flagIcon, button));
                    }
                } else if (controller.isGameOver() && cell.isMine()) {
                    button.setIcon(resizeIcon(mineIcon, button));
                } else {
                    button.setIcon(null);
                    button.setText("");
                }
            }
        }

        if (controller.isGameWon()) {
            JOptionPane.showMessageDialog(this, "Congratulations! You cleared the minefield!", "You Win!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private ImageIcon loadIcon(String path) {
        try {
            URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                BufferedImage img = ImageIO.read(imgURL);
                return new ImageIcon(img);
            } else {
                System.err.println("Error: Image not found - " + path);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return null;
        }
    }

    private ImageIcon resizeIcon(ImageIcon icon, JButton button) {
        if (icon == null) return null;

        Image img = icon.getImage();
        int width = button.getWidth();
        int height = button.getHeight();
        if (width == 0 || height == 0) return icon; // Предотвращаем деление на ноль

        Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }
}