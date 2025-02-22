package calculator;

import calculator.exceptions.MissingPropertiesException;
import org.junit.jupiter.api.Test;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {
    private final File originalFile = new File("src/main/resources/commands.properties");
    private final File backupFile = new File("src/main/resources/commands_backup.properties");

//    @Test
//    void testMissingPropertiesException() throws InterruptedException {
//
//        if (originalFile.exists())
//            assertTrue(originalFile.renameTo(backupFile), "Can`t rename commands.properties");
//
//        Exception exception = assertThrows(MissingPropertiesException.class, CommandFactory::new);
//
//        assertEquals("Error: commands.properties file is missing or cannot be loaded",
//                exception.getMessage());
//
//        if (backupFile.exists())
//            assertTrue(backupFile.renameTo(originalFile), "Can`t rename commands.properties");
//
//        Thread.sleep(2000);
//    }
}