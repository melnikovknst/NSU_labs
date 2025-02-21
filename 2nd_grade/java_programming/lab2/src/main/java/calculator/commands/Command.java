package calculator.commands;

import calculator.Context;
import calculator.exceptions.CalculatorException;

public interface Command {
    void execute(Context context, String[] args) throws CalculatorException;
}