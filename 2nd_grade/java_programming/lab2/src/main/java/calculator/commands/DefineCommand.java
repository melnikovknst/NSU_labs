package calculator.commands;

import calculator.Context;
import calculator.exceptions.InvalidArgumentsException;

public class DefineCommand implements Command {
    @Override
    public void execute(Context context, String[] args) throws InvalidArgumentsException{
        if (args.length != 2) {
            throw new InvalidArgumentsException("Error: DEFINE requires 2 args");
        }
        try {
            double value = Double.parseDouble(args[1]);
            context.define(args[0], value);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("Error: invalid arg " + args[1]);
        }
    }
}