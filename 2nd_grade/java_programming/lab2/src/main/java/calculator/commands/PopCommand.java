package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;

public class PopCommand implements Command {
    @Override
    public void execute(Context context, String[] args) throws StackException {
        if (context.stackSize() == 0)
            throw new StackException("Error: Cannot pop from an empty stack.");

        context.pop();
    }
}
