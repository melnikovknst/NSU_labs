package calculator.commands;

import calculator.Context;

public class AddCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        System.out.println("Add executed");
    }
}