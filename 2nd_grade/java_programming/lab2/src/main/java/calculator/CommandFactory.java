package calculator;

import calculator.commands.Command;
import calculator.exceptions.MissingPropertiesException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CommandFactory {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandFactory() throws MissingPropertiesException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/commands.properties")) {
//            if (input == null) {
//                throw new MissingPropertiesException();
//            }

            properties.load(input);
            for (String key : properties.stringPropertyNames()) {
                Class<?> clazz = Class.forName(properties.getProperty(key));
                commands.put(key, (Command) clazz.getDeclaredConstructor().newInstance());
            }
//        } catch (MissingPropertiesException e) {
//            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public Command getCommand(String command) {
        return commands.get(command);
    }
}