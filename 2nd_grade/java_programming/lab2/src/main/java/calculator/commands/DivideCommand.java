package calculator.commands;

import calculator.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import calculator.exceptions.StackException;
import calculator.exceptions.DivisionByZeroException;

public class DivideCommand implements Command {
    private static final Logger logger = LogManager.getLogger(DivideCommand.class);

    @Override
    public void execute(Context context, String[] args) throws StackException, DivisionByZeroException {
        if (context.stackSize() < 2)
            throw new StackException("Error: Divide command requires 2 or more elements on stack");

        double b = context.pop();

        if (b == 0)
            throw new DivisionByZeroException();

        double a = context.pop();
        double result = a/b;

        context.push(result);
        logger.debug("Divide executed: {}", result);
    }
}
