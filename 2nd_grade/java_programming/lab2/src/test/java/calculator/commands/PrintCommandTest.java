package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrintCommandTest {
    private Context context;
    private PrintCommand printCommand;

    @BeforeEach
    void setUp() {
        context = new Context();
        printCommand = new PrintCommand();
    }

    @Test
    void testPrintTopElement() throws StackException {
        context.push(2.0);
        printCommand.execute(context, new String[]{});

        assertEquals(2.0, context.peek());
    }

    @Test
    void testPrintFromEmptyStackException() {
        Exception exception = assertThrows(StackException.class,
                () -> printCommand.execute(context, new String[]{}));

        assertEquals("Error: Cannot print from an empty stack", exception.getMessage());
    }
}