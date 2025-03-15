package factory.suppliers;

import factory.storage.Storage;
import factory.parts.Part;

public class Supplier<T extends Part> implements Runnable
{
    private final Storage<T> storage;
    private final Class<T> partType;
    private int delay;
    private int suppliedCount = 0;

    public Supplier(Storage<T> storage, Class<T> partType, int delay)
    {
        this.storage = storage;
        this.partType = partType;
        this.delay = delay;
    }

    @Override
    public void run()
    {
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                T part = partType.getDeclaredConstructor().newInstance();
                synchronized (storage)
                {
                    while (storage.getSize() >= storage.getCapacity())
                    {
                        storage.wait();
                    }
                    storage.put(part);
                    suppliedCount++;
                    storage.notify();
                }
                Thread.sleep(delay);
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setDelay(int delay)
    {
        this.delay = delay;
    }

    public int getDelay()
    {
        return delay;
    }

    public int getSuppliedCount()
    {
        return suppliedCount;
    }
}