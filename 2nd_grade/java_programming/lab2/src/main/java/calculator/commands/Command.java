package calculator.commands;

import calculator.Context;
import calculator.exceptions.CalculatorCommandException;

public interface Command {
    void execute(Context context, String[] args) throws CalculatorCommandException;
}