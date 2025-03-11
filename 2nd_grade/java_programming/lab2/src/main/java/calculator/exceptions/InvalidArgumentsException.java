package calculator.exceptions;

public class InvalidArgumentsException extends CalculatorCommandException {
    public InvalidArgumentsException(String message) {
        super(message);
    }
}