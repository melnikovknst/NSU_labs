package calculator.exceptions;

public class NegativeSqrtException extends CalculatorCommandException {
    public NegativeSqrtException(double a) {
        super("Error: Impossible to extract the root from a negative number: " + a);
    }
}