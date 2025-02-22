package calculator.commands;

import calculator.Context;
import calculator.exceptions.InvalidArgumentsException;
import calculator.exceptions.UndefinedVariableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefineCommandTest {
    private Context context;
    private DefineCommand defineCommand;

    @BeforeEach
    void setUp() {
        context = new Context();
        defineCommand = new DefineCommand();
    }

    @Test
    void testDefineVariable() throws InvalidArgumentsException, UndefinedVariableException {
        defineCommand.execute(context, new String[]{"x", "3.0"});
        assertEquals(3.0, context.getVariable("x"));
    }

    @Test
    void testDefineWithInvalidValueThrowsException() {
        Exception exception = assertThrows(InvalidArgumentsException.class,
                () -> defineCommand.execute(context, new String[]{"x", "abc"}));
        assertEquals("Error: invalid arg abc", exception.getMessage());
    }

    @Test
    void testDefineWithWrongArgumentsException() {
        Exception exception = assertThrows(InvalidArgumentsException.class,
                () -> defineCommand.execute(context, new String[]{"x"}));
        assertEquals("Error: DEFINE requires 2 args", exception.getMessage());
    }
}