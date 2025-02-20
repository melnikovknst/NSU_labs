package calculator.commands;

import calculator.Context;

public class PrintCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        if (context.stackSize() == 0)
            throw new RuntimeException("Error: Cannot print from an empty stack.");

        System.out.println(context.peek());
    }
}
