package calculator.commands;

import calculator.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import calculator.exceptions.StackException;

public class SqrtCommand implements Command {
    private static final Logger logger = LogManager.getLogger(SqrtCommand.class);

    @Override
    public void execute(Context context, String[] args) throws StackException {
        if (context.stackSize() == 0)
            throw new StackException("Error: Sqrt command requires 1 or more elements on stack");

        double a = context.pop();
        if (a < 0)
            throw new RuntimeException("Error: Impossible to extract the root from a negative number");

        double value = Math.sqrt(a);
        context.push(value);
        logger.debug("Sqrt executed: {}", value);
    }
}