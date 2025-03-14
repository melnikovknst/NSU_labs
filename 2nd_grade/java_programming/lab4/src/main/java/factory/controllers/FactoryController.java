package factory.controllers;

import factory.storage.Storage;
import factory.workers.*;
import factory.parts.*;

public class FactoryController implements Runnable
{
    private final Storage<Car> carStorage;
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final ThreadPool threadPool;

    public FactoryController(Storage<Car> carStorage, Storage<Body> bodyStorage, Storage<Motor> motorStorage,
                             Storage<Accessory> accessoryStorage, ThreadPool threadPool)
    {
        this.carStorage = carStorage;
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.threadPool = threadPool;
    }

    @Override
    public void run()
    {
        try
        {
            for (int i = 0; i < carStorage.getCapacity(); i++)
            {
                threadPool.submitTask(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage));
            }

            while (!Thread.currentThread().isInterrupted())
            {
                synchronized (this)
                {
                    wait();
                }

                synchronized (carStorage)
                {
                    if (carStorage.getSize() == 0)
                    {
                        threadPool.submitTask(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage));
                    }
                }
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void notifySale()
    {
        notify();
    }
}