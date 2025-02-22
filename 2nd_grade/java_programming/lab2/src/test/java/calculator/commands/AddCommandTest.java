package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddCommandTest {
    private Context context;
    private AddCommand addCommand;

    @BeforeEach
    void setUp() {
        context = new Context();
        addCommand = new AddCommand();
    }

    @Test
    void testAddTwoNumbers() throws StackException {
        context.push(1.0);
        context.push(3.0);
        context.push(7.0);
        addCommand.execute(context, new String[]{});
        assertEquals(10.0, context.pop());
    }

    @Test
    void testAddTwoNegativeNumbers() throws StackException {
        context.push(1.0);
        context.push(-3.0);
        context.push(-7.0);
        addCommand.execute(context, new String[]{});
        assertEquals(-10.0, context.pop());
    }

    @Test
    void testAddWithEmptyStackException() {
        Exception exception = assertThrows(StackException.class, () -> addCommand.execute(context, new String[]{}));
        assertEquals("Error: Add command requires 2 or more elements on stack", exception.getMessage());
    }

    @Test
    void testAddWithOneElementException() {
        context.push(5.0);
        Exception exception = assertThrows(StackException.class, () -> addCommand.execute(context, new String[]{}));
        assertEquals("Error: Add command requires 2 or more elements on stack", exception.getMessage());
    }
}