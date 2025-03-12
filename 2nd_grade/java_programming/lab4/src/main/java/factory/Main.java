package factory;

import factory.parts.*;
import factory.storage.Storage;
import factory.suppliers.Supplier;

public class Main
{
    public static void main(String[] args)
    {
        Storage<Body> bodyStorage = new Storage<>(10);
        Storage<Motor> motorStorage = new Storage<>(10);
        Storage<Accessory> accessoryStorage = new Storage<>(10);

        Thread bodySupplier = new Thread(new Supplier<>(bodyStorage, Body.class, 1000));
        Thread motorSupplier = new Thread(new Supplier<>(motorStorage, Motor.class, 1500));
        Thread accessorySupplier = new Thread(new Supplier<>(accessoryStorage, Accessory.class, 2000));

        bodySupplier.start();
        motorSupplier.start();
        accessorySupplier.start();

        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        bodySupplier.interrupt();
        motorSupplier.interrupt();
        accessorySupplier.interrupt();
    }
}