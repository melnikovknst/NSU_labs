package calculator.exceptions;

public class DivisionByZeroException extends CalculatorCommandException {
    public DivisionByZeroException() {
        super("Error: Division by zero");
    }
}