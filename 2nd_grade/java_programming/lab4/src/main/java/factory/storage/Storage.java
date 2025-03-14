package factory.storage;

import java.util.LinkedList;
import java.util.Queue;

public class Storage<T>
{
    private final int capacity;
    private final Queue<T> items = new LinkedList<>();
    private final Object monitor = new Object();

    public Storage(int capacity)
    {
        this.capacity = capacity;
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
}