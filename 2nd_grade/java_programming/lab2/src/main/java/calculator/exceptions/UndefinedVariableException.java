package calculator.exceptions;

public class UndefinedVariableException extends CalculatorCommandException {
    public UndefinedVariableException(String variable) {
        super("Error: Variable '" + variable + "' is not defined");
    }
}