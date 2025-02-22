package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtractCommandTest {
    private Context context;
    private SubtractCommand subtractCommand;

    @BeforeEach
    void setUp() {
        context = new Context();
        subtractCommand = new SubtractCommand();
    }

    @Test
    void testSubtractTwoNumbers() throws StackException {
        context.push(10.0);
        context.push(4.0);
        subtractCommand.execute(context, new String[]{});

        assertEquals(6.0, context.pop());
    }

    @Test
    void testSubtractNegativeNumbers() throws StackException {
        context.push(-5.0);
        context.push(-3.0);
        subtractCommand.execute(context, new String[]{});

        assertEquals(-2.0, context.pop());
    }

    @Test
    void testSubtractWithZero() throws StackException {
        context.push(7.0);
        context.push(0.0);
        subtractCommand.execute(context, new String[]{});

        assertEquals(7.0, context.pop());
    }

    @Test
    void testSubtractWithNegativeResult() throws StackException {
        context.push(3.0);
        context.push(5.0);
        subtractCommand.execute(context, new String[]{});

        assertEquals(-2.0, context.pop());
    }

    @Test
    void testNotEnoughElementsException() {
        context.push(5.0);

        Exception exception = assertThrows(StackException.class,
                () -> subtractCommand.execute(context, new String[]{}));

        assertEquals("Error: Subtract command requires 2 or more elements on stack", exception.getMessage());
    }
}