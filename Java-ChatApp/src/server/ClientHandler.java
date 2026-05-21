package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {

    private String username;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerGUI server;

    public ClientHandler(Socket socket, ServerGUI server) {
        this.socket = socket;
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public DataOutputStream getOut() {
        return out;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            username = in.readUTF();
            server.log(username + " joined");

            while (true) {
                String type = in.readUTF();

                if (type.equals("TEXT")) {
                    String receiver = in.readUTF();
                    String msg = in.readUTF();

                    server.log(username + " -> " + receiver + ": " + msg);

                    server.getDatabase().saveMessage(username, receiver, msg);

                    ClientHandler target = server.getClientByName(receiver);

                    if (target != null) {
                        target.getOut().writeUTF("TEXT");
                        target.getOut().writeUTF(username + ": " + msg);
                        target.getOut().flush();
                    }

                } else if (type.equals("FILE")) {
                    String receiver = in.readUTF();
                    String fileName = in.readUTF();
                    int fileSize = in.readInt();

                    byte[] fileData = new byte[fileSize];
                    in.readFully(fileData);

                    server.log(username + " sent file to " + receiver + ": " + fileName);

                    server.getDatabase().saveFileMessage(username, receiver, fileName, fileData);

                    ClientHandler target = server.getClientByName(receiver);

                    if (target != null) {
                        target.getOut().writeUTF("FILE");
                        target.getOut().writeUTF(username);
                        target.getOut().writeUTF(fileName);
                        target.getOut().writeInt(fileSize);
                        target.getOut().write(fileData);
                        target.getOut().flush();
                    }
                }
            }

        } catch (Exception e) {
            server.log("Client disconnected");
            server.removeClient(this);

            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}