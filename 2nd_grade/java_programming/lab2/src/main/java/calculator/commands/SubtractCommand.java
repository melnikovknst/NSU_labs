package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;

public class SubtractCommand implements Command {
    @Override
    public void execute(Context context, String[] args) throws StackException {
        if (context.stackSize() < 2)
            throw new StackException("Error: Subtract command requires 2 or more elements on stack.");

        double b = context.pop();
        double a = context.pop();
        context.push(a - b);
    }
}