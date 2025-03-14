package factory.controllers;

import factory.storage.Storage;
import factory.workers.ThreadPool;
import factory.workers.Worker;
import factory.parts.*;

public class FactoryController implements Runnable
{
    private final Storage<Car> carStorage;
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final ThreadPool threadPool;
    private final int minCarThreshold;

    public FactoryController(Storage<Car> carStorage, Storage<Body> bodyStorage, Storage<Motor> motorStorage,
                             Storage<Accessory> accessoryStorage, ThreadPool threadPool, int minCarThreshold)
    {
        this.carStorage = carStorage;
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.threadPool = threadPool;
        this.minCarThreshold = minCarThreshold;
    }

    @Override
    public void run()
    {
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                synchronized (carStorage)
                {
                    while (carStorage.getSize() > minCarThreshold)
                    {
                        carStorage.wait();
                    }
                }

                threadPool.submitTask(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage));

                System.out.println("FactoryController: Added new car production task");
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}