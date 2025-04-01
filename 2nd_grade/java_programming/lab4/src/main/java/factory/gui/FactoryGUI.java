package factory.gui;

import factory.controllers.FactoryController;
import factory.dealers.Dealer;
import factory.storage.Storage;
import factory.suppliers.Supplier;
import factory.workers.ThreadPool;
import factory.parts.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class FactoryGUI extends JFrame
{
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final Storage<Car> carStorage;
    private final Supplier<Body> bodySupplier;
    private final Supplier<Motor> motorSupplier;
    private final Supplier<Accessory> accessorySupplier;
    private final List<Dealer> dealers;
    private final ThreadPool threadPool;
    private final FactoryController factoryController;

    private final JLabel bodyCountLabel;
    private final JLabel motorCountLabel;
    private final JLabel accessoryCountLabel;
    private final JLabel carCountLabel;
    private final JLabel soldCarsLabel;
    private final JLabel queueSizeLabel;
    private final JLabel bodySuppliedLabel;
    private final JLabel motorSuppliedLabel;
    private final JLabel accessorySuppliedLabel;

    private final JSlider bodySpeedSlider;
    private final JSlider motorSpeedSlider;
    private final JSlider accessorySpeedSlider;
    private final JSlider dealerSpeedSlider;
    private final JTextField workerCountField;
    private final JTextField bodyCapacityField;
    private final JTextField motorCapacityField;
    private final JTextField accessoryCapacityField;
    private final JTextField carCapacityField;

    public FactoryGUI(Storage<Body> bodyStorage, Storage<Motor> motorStorage, Storage<Accessory> accessoryStorage,
                      Storage<Car> carStorage, Supplier<Body> bodySupplier, Supplier<Motor> motorSupplier,
                      Supplier<Accessory> accessorySupplier, List<Dealer> dealers, ThreadPool threadPool,
                      FactoryController factoryController)
    {
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage;
        this.bodySupplier = bodySupplier;
        this.motorSupplier = motorSupplier;
        this.accessorySupplier = accessorySupplier;
        this.dealers = dealers;
        this.threadPool = threadPool;
        this.factoryController = factoryController;

        setTitle("Factory Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new GridLayout(7, 1));

        // Suppliers Panel
        JPanel suppliersPanel = new JPanel(new GridLayout(3, 3));
        suppliersPanel.setBorder(BorderFactory.createTitledBorder("Suppliers"));

        bodySpeedSlider = createSlider(bodySupplier.getDelay());
        motorSpeedSlider = createSlider(motorSupplier.getDelay());
        accessorySpeedSlider = createSlider(accessorySupplier.getDelay());

        bodySpeedSlider.addChangeListener(e -> bodySupplier.setDelay(bodySpeedSlider.getValue()));
        motorSpeedSlider.addChangeListener(e -> motorSupplier.setDelay(motorSpeedSlider.getValue()));
        accessorySpeedSlider.addChangeListener(e -> accessorySupplier.setDelay(accessorySpeedSlider.getValue()));

        bodySuppliedLabel = new JLabel("Supplied: 0");
        motorSuppliedLabel = new JLabel("Supplied: 0");
        accessorySuppliedLabel = new JLabel("Supplied: 0");

        suppliersPanel.add(new JLabel("Body Supplier Speed (ms):"));
        suppliersPanel.add(bodySpeedSlider);
        suppliersPanel.add(bodySuppliedLabel);

        suppliersPanel.add(new JLabel("Motor Supplier Speed (ms):"));
        suppliersPanel.add(motorSpeedSlider);
        suppliersPanel.add(motorSuppliedLabel);

        suppliersPanel.add(new JLabel("Accessory Supplier Speed (ms):"));
        suppliersPanel.add(accessorySpeedSlider);
        suppliersPanel.add(accessorySuppliedLabel);

        // Storages Panel
        JPanel storagesPanel = new JPanel(new GridLayout(3, 5));
        storagesPanel.setBorder(BorderFactory.createTitledBorder("Storages"));

        storagesPanel.add(new JLabel(""));
        storagesPanel.add(new JLabel("Bodies"));
        storagesPanel.add(new JLabel("Motors"));
        storagesPanel.add(new JLabel("Accessories"));
        storagesPanel.add(new JLabel("Cars"));

        storagesPanel.add(new JLabel("Capacity:"));
        bodyCapacityField = createCapacityField(bodyStorage);
        motorCapacityField = createCapacityField(motorStorage);
        accessoryCapacityField = createCapacityField(accessoryStorage);
        carCapacityField = createCapacityField(carStorage);

        storagesPanel.add(bodyCapacityField);
        storagesPanel.add(motorCapacityField);
        storagesPanel.add(accessoryCapacityField);
        storagesPanel.add(carCapacityField);

        storagesPanel.add(new JLabel("Stored:"));
        bodyCountLabel = new JLabel();
        motorCountLabel = new JLabel();
        accessoryCountLabel = new JLabel();
        carCountLabel = new JLabel();

        storagesPanel.add(bodyCountLabel);
        storagesPanel.add(motorCountLabel);
        storagesPanel.add(accessoryCountLabel);
        storagesPanel.add(carCountLabel);

        // Workers Panel
        JPanel workersPanel = new JPanel();
        workersPanel.setBorder(BorderFactory.createTitledBorder("Workers"));

        workerCountField = new JTextField(String.valueOf(threadPool.getWorkerCount()), 5);
        workerCountField.addActionListener(e -> threadPool.setWorkerCount(Integer.parseInt(workerCountField.getText())));

        workersPanel.add(new JLabel("Number of Workers:"));
        workersPanel.add(workerCountField);

        // Dealers Panel
        JPanel dealersPanel = new JPanel(new GridLayout(2, 2));
        dealersPanel.setBorder(BorderFactory.createTitledBorder("Dealers"));

        soldCarsLabel = new JLabel();
        queueSizeLabel = new JLabel();

        dealerSpeedSlider = createSlider(dealers.get(0).getDelay());
        dealerSpeedSlider.addChangeListener(e -> {
            for (Dealer dealer : dealers)
            {
                dealer.setDelay(dealerSpeedSlider.getValue());
            }
        });

        dealersPanel.add(new JLabel("Sold Cars:"));
        dealersPanel.add(soldCarsLabel);
        dealersPanel.add(new JLabel("Queue Size:"));
        dealersPanel.add(queueSizeLabel);
        dealersPanel.add(new JLabel("Dealer Speed (ms):"));
        dealersPanel.add(dealerSpeedSlider);

        add(suppliersPanel);
        add(storagesPanel);
        add(workersPanel);
        add(dealersPanel);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Exiting...");
                System.exit(0);
            }
        });

        new Timer(500, e -> updateLabels()).start();
    }

    private JSlider createSlider(int initialValue)
    {
        JSlider slider = new JSlider(0, 5000, initialValue);
        slider.setMajorTickSpacing(1000);
        slider.setPaintTicks(true);
        return slider;
    }

    private JTextField createCapacityField(Storage<?> storage)
    {
        JTextField field = new JTextField(String.valueOf(storage.getCapacity()), 5);
        field.addActionListener(e -> storage.setCapacity(Integer.parseInt(field.getText())));
        return field;
    }

    private void updateLabels()
    {
        bodyCountLabel.setText(String.valueOf(bodyStorage.getSize()));
        motorCountLabel.setText(String.valueOf(motorStorage.getSize()));
        accessoryCountLabel.setText(String.valueOf(accessoryStorage.getSize()));
        carCountLabel.setText(String.valueOf(carStorage.getSize()));

        int totalSoldCars = dealers.stream().mapToInt(Dealer::getSoldCarsCount).sum();
        soldCarsLabel.setText(String.valueOf(totalSoldCars));
        queueSizeLabel.setText(String.valueOf(threadPool.getQueueSize()));

        bodySuppliedLabel.setText("Supplied: " + bodySupplier.getSuppliedCount());
        motorSuppliedLabel.setText("Supplied: " + motorSupplier.getSuppliedCount());
        accessorySuppliedLabel.setText("Supplied: " + accessorySupplier.getSuppliedCount());
    }
}
