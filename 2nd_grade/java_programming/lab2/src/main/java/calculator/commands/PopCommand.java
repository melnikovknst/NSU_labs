package calculator.commands;

import calculator.Context;

public class PopCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        if (context.stackSize() == 0)
            throw new RuntimeException("Error: Cannot pop from an empty stack.");

        context.pop();
    }
}
