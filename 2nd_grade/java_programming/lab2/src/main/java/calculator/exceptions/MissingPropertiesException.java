package calculator.exceptions;

public class MissingPropertiesException extends CalculatorException {
    public MissingPropertiesException() {
        super("Error: commands.properties file is missing or cannot be loaded");
    }
}