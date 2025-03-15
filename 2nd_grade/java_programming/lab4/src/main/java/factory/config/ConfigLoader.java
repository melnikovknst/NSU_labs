package factory.config;

import factory.exceptions.MissingDefaultConfigException;
import factory.exceptions.UserConfigFileNotFoundException;
import factory.exceptions.InvalidConfigException;
import factory.exceptions.FactoryException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ConfigLoader
{
    private static final String DEFAULT_CONFIG_FILE = "src/main/resources/config.txt";
    private static final Set<String> REQUIRED_KEYS = Set.of(
            "body_storage_capacity",
            "motor_storage_capacity",
            "accessory_storage_capacity",
            "car_storage_capacity",
            "supplier_body_delay",
            "supplier_motor_delay",
            "supplier_accessory_delay",
            "dealer_delay",
            "workers",
            "dealer_count"
    );

    public static Properties loadConfig(String[] args)
    {
        if (args.length == 1)
        {
            try
            {
                return loadUserConfig(args[0]);
            }
            catch (UserConfigFileNotFoundException e)
            {
                System.err.println(e.getMessage());
                System.out.print("Do you want to load the default configuration file \"" + DEFAULT_CONFIG_FILE + "\"? (y/n): ");

                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine().trim().toLowerCase();

                if (!"y".equals(input))
                {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
            }
        }

        return loadDefaultConfig();
    }

    private static Properties loadDefaultConfig()
    {
        File file = new File(DEFAULT_CONFIG_FILE);
        if (!file.exists())
        {
            throw new MissingDefaultConfigException();
        }
        return loadConfigFile(DEFAULT_CONFIG_FILE);
    }

    private static Properties loadUserConfig(String filename)
    {
        File file = new File(filename);
        if (!file.exists())
        {
            throw new UserConfigFileNotFoundException(filename);
        }
        return loadConfigFile(filename);
    }

    private static Properties loadConfigFile(String filename)
    {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filename))
        {
            properties.load(fis);
            Set<String> missingKeys = new HashSet<>(REQUIRED_KEYS);
            missingKeys.removeAll(properties.stringPropertyNames());

            if (!missingKeys.isEmpty())
            {
                throw new InvalidConfigException(missingKeys);
            }
            return properties;
        }
        catch (IOException e)
        {
            throw new FactoryException("Error loading configuration: " + e.getMessage());
        }
    }
}