package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiplyCommandTest {
    private Context context;
    private MultiplyCommand multiplyCommand;

    @BeforeEach
    void setUp() {
        context = new Context();
        multiplyCommand = new MultiplyCommand();
    }

    @Test
    void testMultiplyTwoNumbers() throws StackException {
        context.push(3.0);
        context.push(4.0);
        multiplyCommand.execute(context, new String[]{});

        assertEquals(12.0, context.pop());
    }

    @Test
    void testMultiplyWithZero() throws StackException {
        context.push(7.0);
        context.push(0.0);
        multiplyCommand.execute(context, new String[]{});

        assertEquals(0.0, context.pop());
    }

    @Test
    void testMultiplyNegativeNumbers() throws StackException {
        context.push(-2.0);
        context.push(-5.0);
        multiplyCommand.execute(context, new String[]{});

        assertEquals(10.0, context.pop());
    }

    @Test
    void testMultiplyPositiveAndNegative() throws StackException {
        context.push(-3.0);
        context.push(6.0);
        multiplyCommand.execute(context, new String[]{});

        assertEquals(-18.0, context.pop());
    }

    @Test
    void testNotEnoughElementsException() {
        context.push(5.0);

        Exception exception = assertThrows(StackException.class,
                () -> multiplyCommand.execute(context, new String[]{}));

        assertEquals("Error: Multiply command requires 2 or more elements on stack", exception.getMessage());
    }
}