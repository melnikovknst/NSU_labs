package calculator.exceptions;

public class InvalidCommandException extends CalculatorCommandException {
    public InvalidCommandException(String command) {
        super("Error: unknown command '" + command + "'");
    }
}
