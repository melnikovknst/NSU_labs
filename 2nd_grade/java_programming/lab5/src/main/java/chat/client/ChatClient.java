package chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatClient {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JTextField nameField;
    private JButton connectButton;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Thread readerThread;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }

    public ChatClient() {
        frame = new JFrame("Chat Client");
        chatArea = new JTextArea();
        inputField = new JTextField();
        nameField = new JTextField("Enter your name");
        connectButton = new JButton("Connect");

        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(nameField, BorderLayout.CENTER);
        topPanel.add(connectButton, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputField, BorderLayout.SOUTH);

        connectButton.addActionListener(e -> connect());
        inputField.addActionListener(e -> sendMessage());

        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void connect() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) return;

        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            chatArea.append("Connected as " + name + "\n");
            nameField.setEditable(false);
            connectButton.setEnabled(false);

            readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        chatArea.append(line + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("Disconnected.\n");
                }
            });
            readerThread.start();
        } catch (IOException e) {
            chatArea.append("Connection failed: " + e.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (msg.isEmpty()) return;

        try {
            out.write(msg + "\n");
            out.flush();
            inputField.setText("");
        } catch (IOException e) {
            chatArea.append("Send failed: " + e.getMessage() + "\n");
        }
    }
}