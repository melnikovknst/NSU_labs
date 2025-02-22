package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;
import calculator.exceptions.DivisionByZeroException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DivideCommandTest {
    private Context context;
    private DivideCommand divideCommand;

    @BeforeEach
    void setUp() {
        context = new Context();
        divideCommand = new DivideCommand();
    }

    @Test
    void testDivideTwoNumbers() throws StackException, DivisionByZeroException {
        context.push(10.0);
        context.push(2.0);
        divideCommand.execute(context, new String[]{});

        assertEquals(5.0, context.pop());
    }

    @Test
    void testDivideNegativeNumbers() throws StackException, DivisionByZeroException {
        context.push(-15.0);
        context.push(3.0);
        divideCommand.execute(context, new String[]{});

        assertEquals(-5.0, context.pop());
    }

    @Test
    void testDivideByZeroException() {
        context.push(10.0);
        context.push(0.0);

        Exception exception = assertThrows(DivisionByZeroException.class,
                () -> divideCommand.execute(context, new String[]{}));

        assertEquals("Error: Division by zero", exception.getMessage());
    }

    @Test
    void testNotEnoughElementsException() {
        context.push(5.0);

        Exception exception = assertThrows(StackException.class,
                () -> divideCommand.execute(context, new String[]{}));

        assertEquals("Error: Divide command requires 2 or more elements on stack", exception.getMessage());
    }
}