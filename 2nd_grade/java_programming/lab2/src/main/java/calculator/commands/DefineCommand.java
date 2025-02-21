package calculator.commands;

import calculator.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import calculator.exceptions.InvalidArgumentsException;

public class DefineCommand implements Command {
    private static final Logger logger = LogManager.getLogger(DefineCommand.class);

    @Override
    public void execute(Context context, String[] args) throws InvalidArgumentsException{
        if (args.length != 2) {
            throw new InvalidArgumentsException("Error: DEFINE requires 2 args");
        }
        try {
            double value = Double.parseDouble(args[1]);
            context.define(args[0], value);

            logger.debug("DEFINE executed: {} = {}", args[0], args[1]);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("Error: invalid arg " + args[1]);
        }
    }
}