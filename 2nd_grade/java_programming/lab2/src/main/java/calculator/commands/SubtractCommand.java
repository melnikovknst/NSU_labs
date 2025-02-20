package calculator.commands;

import calculator.Context;

public class SubtractCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        System.out.println("Sub executed");
    }
}