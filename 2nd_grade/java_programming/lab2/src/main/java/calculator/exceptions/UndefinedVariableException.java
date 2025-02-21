package calculator.exceptions;

public class UndefinedVariableException extends CalculatorException {
    public UndefinedVariableException(String variable) {
        super("Error: Variable '" + variable + "' is not defined");
    }
}