package calculator.commands;

import calculator.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import calculator.exceptions.StackException;

public class PrintCommand implements Command {
    private static final Logger logger = LogManager.getLogger(PrintCommand.class);

    @Override
    public void execute(Context context, String[] args) throws StackException {
        if (context.stackSize() == 0)
            throw new StackException("Error: Cannot print from an empty stack");

        System.out.println(context.peek());
        logger.debug("PRINT executed");
    }
}
