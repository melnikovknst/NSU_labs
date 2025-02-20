package calculator.commands;

import calculator.Context;

public class PushCommand implements Command {
    @Override
    public void execute(Context context, String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("Error: PUSH requires 1 arg");
        }
        try {
            double value;
            if (context.hasVariable(args[0])) {
                value = context.getVariable(args[0]);
            } else {
                value = Double.parseDouble(args[0]);
            }
            context.push(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error: invalid arg: " + args[0]);
        }
    }
}