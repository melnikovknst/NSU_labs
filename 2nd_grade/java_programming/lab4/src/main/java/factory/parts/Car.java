package factory.parts;

public class Car
{
    private static int counter = 0;
    private final int id;
    private final Body body;
    private final Motor motor;
    private final Accessory accessory;

    public Car(Body body, Motor motor, Accessory accessory)
    {
        this.id = ++counter;
        this.body = body;
        this.motor = motor;
        this.accessory = accessory;
    }

    public int getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "Car #" + id + " (Body: " + body.getId() + ", Motor: " + motor.getId() + ", Accessory: " + accessory.getId() + ")";
    }
}