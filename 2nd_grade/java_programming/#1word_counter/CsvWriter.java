import java.io.*;
import java.util.*;

public class CsvWriter {
    private final String outputFileName;

    public CsvWriter(String outputFile) {
        this.outputFileName = outputFile;
    }

    public void writeToCsv(WordStat stat) {
        Map<String, WordEntry> wordCountMap = stat.getWordCountMap();
        int totalWordCount = wordCountMap.values().stream().mapToInt(WordEntry::getCount).sum();

        List<WordEntry> sortedWords = sortWordsByFrequency(wordCountMap);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            writer.write("Word;Repetitions;%\n");
            for (WordEntry entry : sortedWords) {
                writer.write(String.format("%s;%d;%.2f\n", entry.getWord(), entry.getCount(),
                        (entry.getCount() / (double) totalWordCount) * 100));
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getLocalizedMessage());
        }
    }

    private List<WordEntry> sortWordsByFrequency(Map<String, WordEntry> wordCountMap) {
        List<WordEntry> wordList = new ArrayList<>(wordCountMap.values());
        wordList.sort(Comparator.comparingInt(WordEntry::getCount).reversed());

        return wordList;
    }
}
