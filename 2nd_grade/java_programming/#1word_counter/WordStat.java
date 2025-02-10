import java.io.*;
import java.util.*;

public class WordStat {
    private String inputFileName;
    private Set<WordEntry> wordSet;
    private int wordCounter;

    public WordStat(String inputFileName) {
        this.inputFileName = inputFileName;
        this.wordSet = new HashSet<>();
        this.wordCounter = 0;
    }

    public void textProcessing() {
        processFile();
    }

    private void processFile() {
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(inputFileName));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getLocalizedMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    private void processLine(String line) {
        int len = line.length();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < len; i++) {
            char ch = line.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                word.append(ch);
            } else if (word.length() > 0) {
                addWord(word.toString());
                word.setLength(0);
            }
        }

        if (word.length() > 0) {
            addWord(word.toString());
        }
    }

    private void addWord(String word) {
        WordEntry newEntry = new WordEntry(word);
        if (!wordSet.contains(newEntry)) {
            wordSet.add(newEntry);
        } else {
            for (WordEntry entry : wordSet) {
                if (entry.equals(newEntry)) {
                    entry.incrementCount();
                    break;
                }
            }
        }
        wordCounter++;
    }

    public Set<WordEntry> getWords() {
        return wordSet;
    }

    public int getWordCounter() {
        return wordCounter;
    }
}