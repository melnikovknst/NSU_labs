package calculator.commands;

import calculator.Context;

public class DefineCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length != 2) {
            throw new RuntimeException("Error: DEFINE requires 2 args");
        }
        try {
            double value = Double.parseDouble(args[1]);
            context.define(args[0], value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error: invalid arg " + args[1]);
        }
    }
}