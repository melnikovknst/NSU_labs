package calculator.commands;

import calculator.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import calculator.exceptions.StackException;

public class AddCommand implements Command {
    private static final Logger logger = LogManager.getLogger(AddCommand.class);

    @Override
    public void execute(Context context, String[] args) throws StackException {
        if (context.stackSize() < 2)
            throw new StackException("Error: Add command requires 2 or more elements on stack.");

        double b = context.pop();
        double a = context.pop();
        double result = a + b;

        context.push(result);
        logger.debug("Add executed: {}", result);
    }
}