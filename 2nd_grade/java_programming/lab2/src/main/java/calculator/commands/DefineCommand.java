package calculator.commands;

import calculator.Context;

public class DefineCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        System.out.println("Define executed");
    }
}
