package factory.workers;

import factory.exceptions.InvalidThreadPoolSizeException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadPool
{
    private List<Thread> workers;
    private final Queue<Runnable> taskQueue = new LinkedList<>();
    private boolean isRunning = true;
    private int workerCount;

    public ThreadPool(int workerCount)
    {
        setWorkerCount(workerCount);
    }

    public void submitTask(Runnable task)
    {
        synchronized (this)
        {
            taskQueue.add(task);
            this.notify();
        }
    }

    public void setWorkerCount(int newCount)
    {
        if (newCount <= 0)
        {
            throw new InvalidThreadPoolSizeException(newCount);
        }

        synchronized (this)
        {
            if (workers != null)
            {
                for (Thread worker : workers)
                {
                    worker.interrupt();
                }
            }

            this.workerCount = newCount;
            workers = new LinkedList<>();

            for (int i = 0; i < workerCount; i++)
            {
                Thread workerThread = new Thread(() ->
                {
                    try
                    {
                        while (isRunning)
                        {
                            Runnable task;
                            synchronized (this)
                            {
                                while (taskQueue.isEmpty() && isRunning)
                                {
                                    this.wait();
                                }
                                if (!isRunning)
                                {
                                    break;
                                }
                                task = taskQueue.poll();
                            }
                            if (task != null)
                            {
                                task.run();
                            }
                        }
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                });

                workerThread.start();
                workers.add(workerThread);
            }
        }
    }

    public synchronized int getQueueSize()
    {
        return taskQueue.size();
    }

    public int getWorkerCount()
    {
        return workerCount;
    }

    public void shutdown()
    {
        isRunning = false;
        synchronized (this)
        {
            this.notifyAll();
        }
        for (Thread worker : workers)
        {
            worker.interrupt();
        }
    }
}
