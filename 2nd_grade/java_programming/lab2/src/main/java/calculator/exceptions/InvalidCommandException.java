package calculator.exceptions;

public class InvalidCommandException extends CalculatorException {
    public InvalidCommandException(String command) {
        super("Error: Unknown command '" + command + "'");
    }
}