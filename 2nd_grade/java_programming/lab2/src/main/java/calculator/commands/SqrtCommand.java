package calculator.commands;

import calculator.Context;

public class SqrtCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        if (context.stackSize() == 0)
            throw new RuntimeException("Error: Sqrt command requires 1 or more elements on stack");

        double a = context.pop();
        if (a < 0)
            throw new RuntimeException("Error: Impossible to extract the root from a negative number");

        context.push(Math.sqrt(a));
    }
}