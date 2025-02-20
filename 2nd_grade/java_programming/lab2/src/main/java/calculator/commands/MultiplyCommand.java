package calculator.commands;

import calculator.Context;

public class MultiplyCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        System.out.println("Mult executed");
    }
}