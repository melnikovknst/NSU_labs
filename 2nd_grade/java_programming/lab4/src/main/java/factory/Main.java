package factory;

import factory.config.ConfigLoader;
import factory.exceptions.FactoryException;
import factory.parts.*;
import factory.storage.Storage;
import factory.suppliers.Supplier;
import factory.workers.ThreadPool;
import factory.controllers.FactoryController;
import factory.dealers.Dealer;

import java.util.Properties;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            Properties config = ConfigLoader.loadConfig(args);

            int bodyStorageCapacity = Integer.parseInt(config.getProperty("body_storage_capacity"));
            int motorStorageCapacity = Integer.parseInt(config.getProperty("motor_storage_capacity"));
            int accessoryStorageCapacity = Integer.parseInt(config.getProperty("accessory_storage_capacity"));
            int carStorageCapacity = Integer.parseInt(config.getProperty("car_storage_capacity"));

            int supplierBodyDelay = Integer.parseInt(config.getProperty("supplier_body_delay"));
            int supplierMotorDelay = Integer.parseInt(config.getProperty("supplier_motor_delay"));
            int supplierAccessoryDelay = Integer.parseInt(config.getProperty("supplier_accessory_delay"));
            int dealerDelay = Integer.parseInt(config.getProperty("dealer_delay"));
            int threadPoolSize = Integer.parseInt(config.getProperty("thread_pool_size"));

            Storage<Body> bodyStorage = new Storage<>(bodyStorageCapacity, "Body Storage");
            Storage<Motor> motorStorage = new Storage<>(motorStorageCapacity, "Motor Storage");
            Storage<Accessory> accessoryStorage = new Storage<>(accessoryStorageCapacity, "Accessory Storage");
            Storage<Car> carStorage = new Storage<>(carStorageCapacity, "Car Storage");

            Thread bodySupplier = new Thread(new Supplier<>(bodyStorage, Body.class, supplierBodyDelay));
            Thread motorSupplier = new Thread(new Supplier<>(motorStorage, Motor.class, supplierMotorDelay));
            Thread accessorySupplier = new Thread(new Supplier<>(accessoryStorage, Accessory.class, supplierAccessoryDelay));

            bodySupplier.start();
            motorSupplier.start();
            accessorySupplier.start();

            ThreadPool threadPool = new ThreadPool(threadPoolSize);

            FactoryController factoryController = new FactoryController(carStorage, bodyStorage, motorStorage, accessoryStorage, threadPool);
            Thread factoryControllerThread = new Thread(factoryController);
            factoryControllerThread.start();

            Thread dealer1 = new Thread(new Dealer(carStorage, 1, dealerDelay, true, factoryController));
            Thread dealer2 = new Thread(new Dealer(carStorage, 2, dealerDelay, true, factoryController));

            dealer1.start();
            dealer2.start();

            try
            {
                Thread.sleep(10000);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }

            bodySupplier.interrupt();
            motorSupplier.interrupt();
            accessorySupplier.interrupt();
            threadPool.shutdown();
            factoryControllerThread.interrupt();
            dealer1.interrupt();
            dealer2.interrupt();
        }
        catch (FactoryException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}