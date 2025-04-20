package factory.storage;

import factory.exceptions.StorageEmptyException;
import java.util.LinkedList;
import java.util.Queue;

public class Storage<T>
{
    private final int capacity;
    private final Queue<T> items = new LinkedList<>();

    public Storage(int capacity)
    {
        this.capacity = capacity;
    }

    public void put(T item) throws InterruptedException
    {
        synchronized (this)
        {
            while (items.size() >= capacity)
            {
                this.wait();
            }
            items.add(item);
            this.notifyAll();
        }
    }

    public T take() throws InterruptedException
    {
        synchronized (this)
        {
            while (items.isEmpty())
            {
                this.wait();
            }
            T item = items.poll();
            this.notifyAll();
            return item;
        }
    }

    public int getSize()
    {
        synchronized (this)
        {
            return items.size();
        }
    }

    public int getCapacity()
    {
        return capacity;
    }
}
