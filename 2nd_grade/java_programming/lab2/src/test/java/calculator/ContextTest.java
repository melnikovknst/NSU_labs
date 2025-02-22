package calculator;

import calculator.exceptions.CalculatorException;
import calculator.exceptions.UndefinedVariableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContextTest {
    private Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @Test
    void testPushAndPop() {
        assertEquals(0, context.stackSize());
        context.push(5.0);
        context.push(2.0);
        assertEquals(2, context.stackSize());
        assertEquals(2.0, context.pop());
        assertEquals(1, context.stackSize());
        assertEquals(5.0, context.pop());
        assertEquals(0, context.stackSize());
    }

    @Test
    void testPeek() {
        context.push(10.0);
        assertEquals(10.0, context.peek());
        assertEquals(10.0, context.pop());
    }

    @Test
    void testDefineAndGetVariable() throws UndefinedVariableException {
        context.define("x", 5.14);
        context.define("x", 6.24);
        context.define("z", 7.44);
        assertEquals(6.24, context.getVariable("x"));
        assertEquals(7.44, context.getVariable("z"));
    }

    @Test
    void testHasVariable() {
        context.define("x", 1.0);
        assertTrue(context.hasVariable("x"));
        assertFalse(context.hasVariable("z"));
    }

    @Test
    void UndefinedVariableException() {
        Exception exception = assertThrows(CalculatorException.class, () -> context.getVariable("x"));
        assertEquals("Error: Variable 'x' is not defined", exception.getMessage());
    }
}