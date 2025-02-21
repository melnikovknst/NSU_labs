package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;
import calculator.exceptions.DivisionByZeroException;

public class DivideCommand implements Command {
    @Override
    public void execute(Context context, String[] args) throws StackException, DivisionByZeroException {
        if (context.stackSize() < 2)
            throw new StackException("Error: Divide command requires 2 or more elements on stack.");

        double b = context.pop();

        if (b == 0)
            throw new DivisionByZeroException();

        double a = context.pop();
        context.push(a / b);
    }
}
