package factory.dealers;

import factory.storage.Storage;
import factory.controllers.FactoryController;
import factory.parts.Car;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Dealer implements Runnable
{
    private final Storage<Car> carStorage;
    private final int dealerId;
    private final int delay;
    private final boolean logEnabled;
    private final FactoryController controller;

    public Dealer(Storage<Car> carStorage, int dealerId, int delay, boolean logEnabled, FactoryController controller)
    {
        this.carStorage = carStorage;
        this.dealerId = dealerId;
        this.delay = delay;
        this.logEnabled = logEnabled;
        this.controller = controller;
    }

    @Override
    public void run()
    {
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                Car car = carStorage.take();
                if (logEnabled)
                {
                    logSale(car);
                }

                controller.notifySale();

                Thread.sleep(delay);
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private void logSale(Car car)
    {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logEntry = String.format("%s: Dealer %d: Car %d (Body: %d, Motor: %d, Accessory: %d)%n",
                time, dealerId, car.getId(), car.getBody().getId(), car.getMotor().getId(), car.getAccessory().getId());

        try (FileWriter writer = new FileWriter("factory_log.txt", true))
        {
            writer.write(logEntry);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}