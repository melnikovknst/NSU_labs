package calculator.exceptions;

public class DivisionByZeroException extends CalculatorException {
    public DivisionByZeroException() {
        super("Error: Division by zero");
    }
}