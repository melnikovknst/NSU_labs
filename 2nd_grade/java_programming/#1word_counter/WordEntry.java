import java.util.Objects;

public class WordEntry {
    private final String word;
    private int count;

    public WordEntry(String word) {
        this.word = word;
        this.count = 0;
    }

    public void incrementCount() {
        count++;
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return word + ": " + count;
    }
}
