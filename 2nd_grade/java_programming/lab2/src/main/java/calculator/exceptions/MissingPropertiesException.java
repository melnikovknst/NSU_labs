package calculator.exceptions;

public class MissingPropertiesException extends CalculatorConfigException {
    public MissingPropertiesException() {
        super("Error: commands.properties file is missing or cannot be loaded");
    }
}
