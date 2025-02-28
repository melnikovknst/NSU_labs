package minesweeper.model;

import java.io.*;
import java.util.*;

public class HighScoresManager {
    private static final String FILE_NAME = "highscores.txt";
    private final List<String> highScores;

    public HighScoresManager() {
        highScores = new ArrayList<>();
        loadScores();
    }

    private void loadScores() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                highScores.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading high scores.");
        }
    }

    public void addScore(String playerName, int time) {
        highScores.add(playerName + " - " + time + " seconds");
        highScores.sort(Comparator.comparingInt(s -> Integer.parseInt(s.split(" - ")[1].split(" ")[0])));
        saveScores();
    }

    private void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String score : highScores) {
                writer.write(score);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving high scores.");
        }
    }

    public List<String> getHighScores() {
        return highScores;
    }
}
