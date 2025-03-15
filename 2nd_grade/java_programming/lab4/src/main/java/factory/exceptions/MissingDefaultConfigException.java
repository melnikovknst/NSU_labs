package factory.exceptions;

public class MissingDefaultConfigException extends FactoryException
{
    public MissingDefaultConfigException()
    {
        super("Error: Default configuration file (config.txt) is missing in src/main/resources.");
    }
}