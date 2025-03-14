package factory.workers;

import factory.storage.Storage;
import factory.parts.*;

public class Worker implements Runnable
{
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final Storage<Car> carStorage;

    public Worker(Storage<Body> bodyStorage, Storage<Motor> motorStorage, Storage<Accessory> accessoryStorage, Storage<Car> carStorage)
    {
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage;
    }

    @Override
    public void run()
    {
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                Body body;
                Motor motor;
                Accessory accessory;

                synchronized (bodyStorage)
                {
                    while (bodyStorage.getSize() == 0)
                    {
                        bodyStorage.wait();
                    }
                    body = bodyStorage.take();
                    bodyStorage.notifyAll();
                }

                synchronized (motorStorage)
                {
                    while (motorStorage.getSize() == 0)
                    {
                        motorStorage.wait();
                    }
                    motor = motorStorage.take();
                    motorStorage.notifyAll();
                }

                synchronized (accessoryStorage)
                {
                    while (accessoryStorage.getSize() == 0)
                    {
                        accessoryStorage.wait();
                    }
                    accessory = accessoryStorage.take();
                    accessoryStorage.notifyAll();
                }

                Car car = new Car(body, motor, accessory);

                synchronized (carStorage)
                {
                    while (carStorage.getSize() >= carStorage.getCapacity())
                    {
                        carStorage.wait();
                    }
                    carStorage.put(car);
                    System.out.println("Built: " + car);
                    carStorage.notifyAll();
                }
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}