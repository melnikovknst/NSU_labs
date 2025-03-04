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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        WordEntry wordEntry = (WordEntry) obj;

        return Objects.equals(word, wordEntry.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    @Override
    public String toString() {
        return word + ": " + count;
    }
}
