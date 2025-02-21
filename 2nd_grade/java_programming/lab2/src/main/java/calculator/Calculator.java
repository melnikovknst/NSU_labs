package calculator;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

import calculator.commands.Command;
import calculator.exceptions.CalculatorException;
import calculator.exceptions.InvalidCommandException;


public class Calculator {
    public static void main(String[] args) throws InvalidCommandException {
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

            if (command == null)
                throw new InvalidCommandException(tokens[0]);

            try {
                command.execute(context, java.util.Arrays.copyOfRange(tokens, 1, tokens.length));
            } catch (CalculatorException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}