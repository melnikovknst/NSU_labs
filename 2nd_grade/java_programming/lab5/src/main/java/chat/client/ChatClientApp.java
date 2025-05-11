package chat.client;

import javax.swing.*;

public class ChatClientApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            while (true) {
                LoginDialog dialog = new LoginDialog(null);
                dialog.setVisible(true);

                if (!dialog.isConfirmed()) {
                    System.exit(0);
                }

                String name = dialog.getUserName();
                String host = dialog.getHost();
                int port = dialog.getPort();
                String protocol = dialog.getProtocol();

                try {
                    if ("json".equals(protocol)) {
                        new ChatWindowJson(name, host, port);
                    } else if ("object".equals(protocol)) {
                        new ChatWindowObject(name, host, port);
                    } else {
                        JOptionPane.showMessageDialog(null, "Unsupported protocol: " + protocol);
                        continue;
                    }
                    break;
                } catch (Exception ex) {
                    new ProtocolMismatchDialog();
                }
            }
        });
    }
}
