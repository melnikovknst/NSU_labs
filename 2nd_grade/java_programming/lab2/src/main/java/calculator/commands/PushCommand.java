package calculator.commands;

import calculator.Context;
import calculator.exceptions.InvalidArgumentsException;
import calculator.exceptions.UndefinedVariableException;

public class PushCommand implements Command {
    @Override
    public void execute(Context context, String[] args) throws InvalidArgumentsException, UndefinedVariableException {
        if (args.length != 1) {
            throw new InvalidArgumentsException("Error: PUSH requires 1 arg");
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
            throw new UndefinedVariableException(args[0]);
        }
    }
}