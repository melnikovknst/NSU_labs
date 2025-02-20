package calculator.commands;

import calculator.Context;

public class SqrtCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        System.out.println("Sqrt executed");
    }
}