package calculator;

import calculator.exceptions.InvalidCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayInputStream emptyInputStream = new ByteArrayInputStream("".getBytes());

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setIn(emptyInputStream);
    }

    @Test
    void testCalculatorReadsFromFile() {
        String testFileName = "test_commands.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFileName))) {
            writer.write("PUSH 5\n");
            writer.write("PUSH 3\n");
            writer.write("+\n");
            writer.write("PRINT\n");
        } catch (IOException e) {
            fail("Creation txt file failed");
        }

        assertDoesNotThrow(() -> Calculator.main(new String[]{testFileName}));

        String output = outputStream.toString().trim();
        assertTrue(output.contains("8.0"));
    }

}