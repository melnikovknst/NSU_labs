package calculator;

import java.io.*;
import java.util.Scanner;

import calculator.commands.Command;
import calculator.exceptions.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Calculator {
    private static final Logger logger = LogManager.getLogger(Calculator.class);

    private final CommandFactory factory;
    private final Context context;
    private final Scanner scanner;

    public Calculator(Scanner scanner) throws CalculatorConfigException {
        this.factory = new CommandFactory();
        this.context = new Context();
        this.scanner = scanner;
    }

    public void run() {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] tokens = line.split(" ");
            Command command = factory.getCommand(tokens[0]);

            if (command == null) {
                logger.warn("Unknown command: {}", tokens[0]);
                continue;
            }

            try {
                logger.info("Executing command: {}", tokens[0]);
                command.execute(context, java.util.Arrays.copyOfRange(tokens, 1, tokens.length));
            } catch (CalculatorCommandException e) {
                logger.error("Command execution error: {}", e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = null;

        if (args.length > 0) {
            try {
                scanner = new Scanner(new File(args[0]));
                logger.info("Reading commands from file: {}", args[0]);
            } catch (FileNotFoundException e) {
                logger.error("Error: no such file - {}", args[0]);
                System.exit(1);
            }
        } else {
            scanner = new Scanner(System.in);
            logger.info("Reading commands from standard input");
        }

        try {
            Calculator calc = new Calculator(scanner);
            calc.run();
        } catch (CalculatorConfigException e) {
            logger.error("Configuration error: {}", e.getMessage());
        }
    }
}
