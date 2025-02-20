package calculator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import calculator.commands.Command;


public class Calculator {
    public static void main(String[] args) {
        CommandFactory factory = new CommandFactory();
        Context context = new Context();
        Scanner scanner = null;

        if (args.length > 0) {
            try {
                scanner = new Scanner(new File(args[0]));
            } catch (FileNotFoundException e) {
                System.err.println("Error: no such file - " + args[0]);
                System.exit(1);
            }
        } else {
            scanner = new Scanner(System.in);
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] tokens = line.split(" ");
            Command command = factory.getCommand(tokens[0]);

            if (command == null) {
                System.err.println("Error: Unknown command " + tokens[0]);
                continue;
            }
            try {
                command.execute(context, java.util.Arrays.copyOfRange(tokens, 1, tokens.length));
            } catch (RuntimeException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}