package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import util.Constants;

public class ClientGUI extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton, fileButton;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientGUI() {
        setTitle("Client");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        fileButton = new JButton("File");

        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        panel.add(fileButton, BorderLayout.WEST);

        add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        fileButton.addActionListener(e -> sendFile());

        setVisible(true);

        connect();
        readMessages();
    }

    void connect() {
        try {
            socket = new Socket("localhost", Constants.PORT);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            String username = JOptionPane.showInputDialog(this, "Enter your name:");
            out.writeUTF(username);
            out.flush();

            chatArea.append("Connected as " + username + "\n");

        } catch (Exception e) {
            chatArea.append("Connection failed\n");
        }
    }

    void readMessages(){
        new Thread(() -> {
            try {
                while (true) {
                    String type = in.readUTF();

                    if (type.equals("TEXT")) {
                        String msg = in.readUTF();

                        chatArea.append(msg + "\n");

                    }
                    else if(type.equals("FILE")){

                        String sender = in.readUTF();
                        String fileName = in.readUTF();
                        int fileSize = in.readInt();

                        byte[] fileData = new byte[fileSize];
                        in.readFully(fileData);

                        File downloadsDir = new File("downloads");
                        if (!downloadsDir.exists()) downloadsDir.mkdir();

                        File savedFile = new File(downloadsDir, fileName);

                        FileOutputStream fos = new FileOutputStream(savedFile);
                        fos.write(fileData);
                        fos.close();

                        if (isImageFile(fileName)) {

                            appendText(sender + " sent image: " + fileName);

                            ImageIcon icon = new ImageIcon(savedFile.getAbsolutePath());
                            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);

                            JOptionPane.showMessageDialog(
                                    this,
                                    new JLabel(new ImageIcon(img)),
                                    "Image from " + sender,
                                    JOptionPane.PLAIN_MESSAGE
                            );

                        } else {

                            appendText(sender + " sent file: " + fileName);

                            int open = JOptionPane.showConfirmDialog(
                                    this,
                                    "Open file: " + fileName + "?",
                                    "Document Received",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (open == JOptionPane.YES_OPTION) {
                                Desktop.getDesktop().open(savedFile);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                chatArea.append("Disconnected\n");
            }
        }).start();
    }

    void sendMessage() {
        try {
            String msg = messageField.getText().trim();

            if (msg.isEmpty()) return;

            String receiver = JOptionPane.showInputDialog(this, "Send to:");

            if (receiver == null || receiver.trim().isEmpty()) return;

            out.writeUTF("TEXT");
            out.writeUTF(receiver);
            out.writeUTF(msg);
            out.flush();

            chatArea.append("You: " + msg + "\n");
            messageField.setText("");

        } catch (Exception e) {
            chatArea.append("Send error\n");
        }
    }

    void sendFile() {
        try {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);

            if (result != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();
            String receiver = JOptionPane.showInputDialog(this, "Send to:");

            if (receiver == null || receiver.trim().isEmpty()) return;

            FileInputStream fis = new FileInputStream(file);
            byte[] fileBytes = fis.readAllBytes();
            fis.close();

            out.writeUTF("FILE");
            out.writeUTF(receiver);
            out.writeUTF(file.getName());
            out.writeInt(fileBytes.length);
            out.write(fileBytes);
            out.flush();

            chatArea.append("You sent file: " + file.getName() + "\n");

            if (isImageFile(file.getName())) {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);

                JOptionPane.showMessageDialog(
                        this,
                        new JLabel(new ImageIcon(img)),
                        "Your Image",
                        JOptionPane.PLAIN_MESSAGE
                );
            }

        } catch (Exception e) {
            chatArea.append("File send error\n");
        }
    }

    boolean isImageFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".png") ||
               lower.endsWith(".jpg") ||
               lower.endsWith(".jpeg") ||
               lower.endsWith(".gif") ||
               lower.endsWith(".bmp");
    }

    void appendText(String text) {
        chatArea.setText(chatArea.getText() + text + "\n");
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}