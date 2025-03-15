package factory.exceptions;

public class StorageEmptyException extends FactoryException
{
    public StorageEmptyException(String storageName)
    {
        super("Error: Attempted to take an item from an empty storage: " + storageName);
    }
}