package chat.client;

import javax.swing.*;
import java.awt.*;

public class ProtocolMismatchDialog extends JDialog {
    public ProtocolMismatchDialog() {
        setTitle("Protocol Mismatch");
        setLayout(new BorderLayout());

        JLabel label = new JLabel(
                "<html><center><b>Protocol Error</b><br>Client and server are using different protocols.<br>" +
                        "Please check your settings and try again.</center></html>",
                SwingConstants.CENTER
        );
        label.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton ok = new JButton("OK");
        ok.addActionListener(e -> dispose());

        add(label, BorderLayout.CENTER);
        add(ok, BorderLayout.SOUTH);

        setSize(350, 160);
        setLocationRelativeTo(null);
        setModal(true);
        setVisible(true);
    }
}
