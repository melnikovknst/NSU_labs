package factory.workers;

import factory.exceptions.InvalidThreadPoolSizeException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadPool
{
    private final List<Thread> workers;
    private final Queue<Runnable> taskQueue;
    private boolean isRunning = true;

    public ThreadPool(int workerCount)
    {
        if (workerCount <= 0)
        {
            throw new InvalidThreadPoolSizeException(workerCount);
        }

        workers = new LinkedList<>();
        taskQueue = new LinkedList<>();

        for (int i = 0; i < workerCount; i++)
        {
            Thread workerThread = new Thread(() ->
            {
                try
                {
                    while (isRunning)
                    {
                        Runnable task;
                        synchronized (taskQueue)
                        {
                            while (taskQueue.isEmpty() && isRunning)
                            {
                                taskQueue.wait();
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

    public void submitTask(Runnable task)
    {
        synchronized (taskQueue)
        {
            taskQueue.add(task);
            taskQueue.notify();
        }
    }

    public void shutdown()
    {
        isRunning = false;
        synchronized (taskQueue)
        {
            taskQueue.notifyAll();
        }
        for (Thread worker : workers)
        {
            worker.interrupt();
        }
    }
}