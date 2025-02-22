package calculator.commands;

import calculator.Context;
import calculator.exceptions.StackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PopCommandTest {
    private Context context;
    private PopCommand popCommand;

    @BeforeEach
    void setUp() {
        context = new Context();
        popCommand = new PopCommand();
    }

    @Test
    void testPop() throws StackException {
        context.push(10.0);
        context.push(20.0);

        popCommand.execute(context, new String[]{});

        assertEquals(10.0, context.pop());
    }

    @Test
    void testPopOnEmptyStackException() {
        Exception exception = assertThrows(StackException.class,
                () -> popCommand.execute(context, new String[]{}));

        assertEquals("Error: Cannot pop from an empty stack", exception.getMessage());
    }

    @Test
    void testPopSingleElement() throws StackException {
        context.push(42.0);
        popCommand.execute(context, new String[]{});

        Exception exception = assertThrows(StackException.class,
                () -> popCommand.execute(context, new String[]{}));

        assertEquals("Error: Cannot pop from an empty stack", exception.getMessage());
    }
}
