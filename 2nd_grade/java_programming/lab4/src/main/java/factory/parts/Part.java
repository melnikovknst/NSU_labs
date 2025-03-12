package factory.parts;

public abstract class Part
{
    private static int counter = 0;
    private final int id;

    public Part()
    {
        this.id = ++counter;
    }

    public int getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() + " #" + id;
    }
}