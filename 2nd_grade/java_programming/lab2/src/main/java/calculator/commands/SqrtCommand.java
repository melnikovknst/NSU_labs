package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;

public class SqrtCommand implements Command {
    @Override
    public void execute(Context context, String[] args) throws StackException {
        if (context.stackSize() == 0)
            throw new StackException("Error: Sqrt command requires 1 or more elements on stack");

        double a = context.pop();
        if (a < 0)
            throw new RuntimeException("Error: Impossible to extract the root from a negative number");

        context.push(Math.sqrt(a));
    }
}