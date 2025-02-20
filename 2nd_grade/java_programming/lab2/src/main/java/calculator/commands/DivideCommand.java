package calculator.commands;

import calculator.Context;

public class DivideCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        System.out.println("Div executed");
    }
}
