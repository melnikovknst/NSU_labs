public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Using: java Main <input_file> <output_file>");
            return;
        }

        WordStat wordStat = new WordStat(args[0]);
        CsvWriter writer = new CsvWriter(args[1]);

        wordStat.textProcessing();
        writer.writeToCsv(wordStat);
    }
}