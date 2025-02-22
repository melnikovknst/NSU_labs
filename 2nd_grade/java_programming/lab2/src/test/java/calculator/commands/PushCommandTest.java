package calculator.commands;

import calculator.Context;
import calculator.exceptions.InvalidArgumentsException;
import calculator.exceptions.UndefinedVariableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PushCommandTest {
    private Context context;
    private PushCommand pushCommand;

    @BeforeEach
    void setUp() {
        context = new Context();
        pushCommand = new PushCommand();
    }

    @Test
    void testPushNumber() throws InvalidArgumentsException, UndefinedVariableException {
        pushCommand.execute(context, new String[]{"5.0"});
        assertEquals(5.0, context.pop());
    }

    @Test
    void testPushVariable() throws InvalidArgumentsException, UndefinedVariableException {
        context.define("a", 42.0);
        pushCommand.execute(context, new String[]{"a"});
        assertEquals(42.0, context.pop());
    }

    @Test
    void testPushUndefinedVariableThrowsException() {
        Exception exception = assertThrows(UndefinedVariableException.class,
                () -> pushCommand.execute(context, new String[]{"b"}));
        assertEquals("Error: Variable 'b' is not defined", exception.getMessage());
    }

    @Test
    void testPushInvalidArgumentThrowsException() {
        Exception exception = assertThrows(InvalidArgumentsException.class,
                () -> pushCommand.execute(context, new String[]{}));
        assertEquals("Error: PUSH requires 1 arg", exception.getMessage());
    }
}