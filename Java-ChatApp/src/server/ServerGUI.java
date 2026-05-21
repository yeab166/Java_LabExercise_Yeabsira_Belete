package server;

import javax.swing.*;
import java.awt.BorderLayout;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import util.Constants;

public class ServerGUI extends JFrame {

    private JTextArea chatArea;

    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    private Database database;

    public ClientHandler getClientByName(String name) {
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public ServerGUI() {
        setTitle("Server");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        setVisible(true);

        database = new Database();

        startServer();
    }

    public Database getDatabase() {
        return database;
    }

    public void log(String msg) {
        chatArea.append(msg + "\n");
    }

    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            try {
                if (client != sender) {
                    client.getOut().writeUTF("TEXT");
                    client.getOut().writeUTF(message);
                }
            } catch (Exception e) {
                log("Broadcast error");
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    void startServer() {
        try {
            serverSocket = new ServerSocket(Constants.PORT);
            log("Server started...");

            while (true) {
                Socket socket = serverSocket.accept();
                log("Client connected");

                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
                handler.start();
            }

        } catch (Exception e) {
            log("Server error");
        }
    }

    public static void main(String[] args) {
        new ServerGUI();
    }
}