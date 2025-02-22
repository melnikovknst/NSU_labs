package calculator;

import calculator.exceptions.MissingPropertiesException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {
    private final File originalFile = new File("target/classes/commands.properties");
    private final File backupFile = new File("target/classes/commands_backup.properties");

    @BeforeEach
    void renamePropertiesFile() {
        if (originalFile.exists()) {
            assertTrue(originalFile.renameTo(backupFile), "Can`t rename commands.properties");
        }
    }

    @AfterEach
    void restorePropertiesFile() {
        if (backupFile.exists()) {
            assertTrue(backupFile.renameTo(originalFile), "Can`t rename commands.properties");
        }
    }

    @Test
    void testMissingPropertiesException() throws InterruptedException {
        Exception exception = assertThrows(MissingPropertiesException.class, CommandFactory::new);

        assertEquals("Error: commands.properties file is missing or cannot be loaded",
                exception.getMessage());
    }
}