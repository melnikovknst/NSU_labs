package calculator;

import java.io.*;
import java.util.Scanner;
import java.io.FileNotFoundException;

import calculator.commands.Command;
import calculator.exceptions.MissingPropertiesException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import calculator.exceptions.CalculatorException;
import calculator.exceptions.InvalidCommandException;



public class Calculator {
    private static final Logger logger = LogManager.getLogger(Calculator.class);

    public static void main(String[] args) throws InvalidCommandException, MissingPropertiesException {
        CommandFactory factory = new CommandFactory();
        Context context = new Context();
        Scanner scanner = null;

        if (args.length > 0) {
            try {
                scanner = new Scanner(new File(args[0]));
                logger.info("Reading commands from file: {}", args[0]);
            } catch (FileNotFoundException e) {
                logger.error("Error: no such file -  {}", args[0]);
                System.exit(1);
            }
        } else {
            scanner = new Scanner(System.in);
            logger.info("Reading commands from standard input");
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] tokens = line.split(" ");
            Command command = factory.getCommand(tokens[0]);

            if (command == null)
                throw new InvalidCommandException(tokens[0]);

            try {
                logger.info("Executing command: {}", tokens[0]);
                command.execute(context, java.util.Arrays.copyOfRange(tokens, 1, tokens.length));
            } catch (CalculatorException e) {
                logger.error("Executing error: {}", e.getMessage());
            }
        }
    }
}