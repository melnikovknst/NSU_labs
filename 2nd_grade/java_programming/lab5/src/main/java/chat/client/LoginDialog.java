package chat.client;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private final JTextField nameField = new JTextField("user");
    private final JTextField hostField = new JTextField("localhost");
    private final JTextField portField = new JTextField("12345");
    private final JComboBox<String> protocolBox = new JComboBox<>(new String[]{"JSON", "ObjectStream"});

    private boolean confirmed = false;

    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        setLayout(new BorderLayout());

        JPanel fields = new JPanel(new GridLayout(4, 2, 5, 5));
        fields.add(new JLabel("Name:"));
        fields.add(nameField);
        fields.add(new JLabel("Host:"));
        fields.add(hostField);
        fields.add(new JLabel("Port:"));
        fields.add(portField);
        fields.add(new JLabel("Protocol:"));
        fields.add(protocolBox);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        add(fields, BorderLayout.CENTER);
        add(connectButton, BorderLayout.SOUTH);

        setSize(300, 200);
        setLocationRelativeTo(null);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getUserName() {
        return nameField.getText().trim();
    }

    public String getHost() {
        return hostField.getText().trim();
    }

    public int getPort() {
        return Integer.parseInt(portField.getText().trim());
    }

    public String getProtocol() {
        return ((String) protocolBox.getSelectedItem()).toLowerCase();
    }
}
