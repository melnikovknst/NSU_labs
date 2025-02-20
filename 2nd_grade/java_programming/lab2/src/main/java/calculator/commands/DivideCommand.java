package calculator.commands;

import calculator.Context;

public class DivideCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        if (context.stackSize() < 2)
            throw new RuntimeException("Error: Divide command requires 2 or more elements on stack.");

        double b = context.pop();
        double a = context.pop();
        context.push(a / b);
    }
}
