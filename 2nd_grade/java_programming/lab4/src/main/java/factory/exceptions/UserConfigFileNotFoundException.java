package factory.exceptions;

public class UserConfigFileNotFoundException extends FactoryException
{
    public UserConfigFileNotFoundException(String filename)
    {
        super("Error: User-specified configuration file \"" + filename + "\" not found.");
    }
}