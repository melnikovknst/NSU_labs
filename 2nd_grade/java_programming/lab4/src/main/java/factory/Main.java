package factory;

import factory.config.ConfigLoader;
import factory.parts.*;
import factory.storage.Storage;
import factory.suppliers.Supplier;
import factory.workers.ThreadPool;
import factory.controllers.FactoryController;
import factory.dealers.Dealer;
import factory.gui.FactoryGUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main
{
    public static void main(String[] args)
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
        int workers = Integer.parseInt(config.getProperty("workers"));
        int dealerCount = Integer.parseInt(config.getProperty("dealer_count"));

        Storage<Body> bodyStorage = new Storage<>(bodyStorageCapacity, "Body Storage");
        Storage<Motor> motorStorage = new Storage<>(motorStorageCapacity, "Motor Storage");
        Storage<Accessory> accessoryStorage = new Storage<>(accessoryStorageCapacity, "Accessory Storage");
        Storage<Car> carStorage = new Storage<>(carStorageCapacity, "Car Storage");

        Supplier<Body> bodySupplier = new Supplier<>(bodyStorage, Body.class, supplierBodyDelay);
        Supplier<Motor> motorSupplier = new Supplier<>(motorStorage, Motor.class, supplierMotorDelay);
        Supplier<Accessory> accessorySupplier = new Supplier<>(accessoryStorage, Accessory.class, supplierAccessoryDelay);

        Thread bodySupplierThread = new Thread(bodySupplier);
        Thread motorSupplierThread = new Thread(motorSupplier);
        Thread accessorySupplierThread = new Thread(accessorySupplier);

        bodySupplierThread.start();
        motorSupplierThread.start();
        accessorySupplierThread.start();

        ThreadPool threadPool = new ThreadPool(workers);

        FactoryController factoryController = new FactoryController(carStorage, bodyStorage, motorStorage, accessoryStorage, threadPool);
        Thread factoryControllerThread = new Thread(factoryController);
        factoryControllerThread.start();

        List<Dealer> dealers = new ArrayList<>();
        List<Thread> dealerThreads = new ArrayList<>();
        for (int i = 1; i <= dealerCount; i++)
        {
            Dealer dealer = new Dealer(carStorage, i, dealerDelay, true, factoryController);
            dealers.add(dealer);
            Thread dealerThread = new Thread(dealer);
            dealerThreads.add(dealerThread);
            dealerThread.start();
        }

        SwingUtilities.invokeLater(() -> new FactoryGUI(
                bodyStorage, motorStorage, accessoryStorage, carStorage,
                bodySupplier, motorSupplier, accessorySupplier,
                dealers, threadPool, factoryController
        ).setVisible(true));
    }
}