import java.io.*;
import java.util.*;

public class CsvWriter {
    private String outputFileName;

    public CsvWriter(String outputFile) {
        this.outputFileName = outputFile;
    }

    public void writeToCsv(WordStat stat) {
        Set<WordEntry> words = stat.getWords();
        int counter = stat.getWordCounter();

        List<WordEntry> sortedWords = new ArrayList<>(words);
        sortedWords.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            writer.write("Word;Repetitions;%\n");
            for (WordEntry entry : sortedWords) {
                String word = entry.getWord();
                int count = entry.getCount();
                double percentage = (double) count / counter * 100;

                writer.write(String.format("%s;%d;%.2f%n", word, count, percentage));
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getLocalizedMessage());
        }
    }
}