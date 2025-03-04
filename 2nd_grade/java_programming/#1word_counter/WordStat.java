import java.io.*;
import java.util.*;

public class WordStat {
    private final String inputFileName;
    private final Map<String, WordEntry> wordCountMap;

    public WordStat(String inputFileName) {
        this.inputFileName = inputFileName;
        this.wordCountMap = new HashMap<>();
    }

    public void textProcessing() {
        readFromFileAndProcessWords();
    }

    private void readFromFileAndProcessWords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getLocalizedMessage());
        }
    }

    private void processLine(String line) {
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                word.append(ch);
            } else if (word.length() > 0) {
                addWordToMap(word.toString());
                word.setLength(0);
            }
        }

        if (word.length() > 0) {
            addWordToMap(word.toString());
        }
    }

    private void addWordToMap(String word) {
        word = word.toLowerCase();
        wordCountMap.computeIfAbsent(word, WordEntry::new).incrementCount();
    }

    public Map<String, WordEntry> getWordCountMap() {
        return wordCountMap;
    }
}
