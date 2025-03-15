package factory.storage;

import factory.exceptions.StorageEmptyException;
import java.util.LinkedList;
import java.util.Queue;

public class Storage<T>
{
    private int capacity;
    private final Queue<T> items = new LinkedList<>();
    private final Object monitor = new Object();
    private final String name;

    public Storage(int capacity, String name)
    {
        this.capacity = capacity;
        this.name = name;
    }

    public void put(T item) throws InterruptedException
    {
        synchronized (monitor)
        {
            while (items.size() >= capacity)
            {
                monitor.wait();
            }
            items.add(item);
            monitor.notify();
        }
    }

    public T take() throws InterruptedException
    {
        synchronized (monitor)
        {
            while (items.isEmpty())
            {
                monitor.wait();
            }
            if (items.isEmpty())
            {
                throw new StorageEmptyException(name);
            }
            T item = items.poll();
            monitor.notify();
            return item;
        }
    }

    public int getSize()
    {
        synchronized (monitor)
        {
            return items.size();
        }
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void setCapacity(int capacity)
    {
        synchronized (monitor)
        {
            this.capacity = capacity;
        }
    }
}