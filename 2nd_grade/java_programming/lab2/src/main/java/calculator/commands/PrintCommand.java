package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;

public class PrintCommand implements Command {
    @Override
    public void execute(Context context, String[] args) throws StackException {
        if (context.stackSize() == 0)
            throw new StackException("Error: Cannot print from an empty stack.");

        System.out.println(context.peek());
    }
}
