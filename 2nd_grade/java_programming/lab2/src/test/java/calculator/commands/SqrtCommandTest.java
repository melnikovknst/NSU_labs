package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;
import calculator.exceptions.NegativeSqrtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqrtCommandTest {
    private Context context;
    private SqrtCommand sqrtCommand;

    @BeforeEach
    void setUp() {
        context = new Context();
        sqrtCommand = new SqrtCommand();
    }

    @Test
    void testSqrtPositiveNumber() throws StackException, NegativeSqrtException {
        context.push(9.0);
        sqrtCommand.execute(context, new String[]{});

        assertEquals(3.0, context.pop());
    }

    @Test
    void testSqrtZero() throws StackException, NegativeSqrtException{
        context.push(0.0);
        sqrtCommand.execute(context, new String[]{});

        assertEquals(0.0, context.pop());
    }

    @Test
    void testSqrtNegativeNumberException() {
        context.push(-4.0);

        Exception exception = assertThrows(NegativeSqrtException.class,
                () -> sqrtCommand.execute(context, new String[]{}));

        assertEquals("Error: Impossible to extract the root from a negative number: -4.0", exception.getMessage());
    }

    @Test
    void testSqrtEmptyStackException() {
        Exception exception = assertThrows(StackException.class,
                () -> sqrtCommand.execute(context, new String[]{}));

        assertEquals("Error: Sqrt command requires 1 or more elements on stack", exception.getMessage());
    }
}