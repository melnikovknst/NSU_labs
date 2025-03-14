package factory;

import factory.parts.*;
import factory.storage.Storage;
import factory.suppliers.Supplier;
import factory.workers.ThreadPool;
import factory.controllers.FactoryController;
import factory.dealers.Dealer;

public class Main
{
    public static void main(String[] args)
    {
        Storage<Body> bodyStorage = new Storage<>(10);
        Storage<Motor> motorStorage = new Storage<>(10);
        Storage<Accessory> accessoryStorage = new Storage<>(10);
        Storage<Car> carStorage = new Storage<>(5);

        Thread bodySupplier = new Thread(new Supplier<>(bodyStorage, Body.class, 500));
        Thread motorSupplier = new Thread(new Supplier<>(motorStorage, Motor.class, 700));
        Thread accessorySupplier = new Thread(new Supplier<>(accessoryStorage, Accessory.class, 900));

        bodySupplier.start();
        motorSupplier.start();
        accessorySupplier.start();

        ThreadPool threadPool = new ThreadPool(3);

        FactoryController factoryController = new FactoryController(carStorage, bodyStorage, motorStorage, accessoryStorage, threadPool);
        Thread factoryControllerThread = new Thread(factoryController);
        factoryControllerThread.start();

        Thread dealer1 = new Thread(new Dealer(carStorage, 1, 1500, true, factoryController));
        Thread dealer2 = new Thread(new Dealer(carStorage, 2, 1000, true, factoryController));

        dealer1.start();
        dealer2.start();

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
        threadPool.shutdown();
        factoryControllerThread.interrupt();
        dealer1.interrupt();
        dealer2.interrupt();
    }
}