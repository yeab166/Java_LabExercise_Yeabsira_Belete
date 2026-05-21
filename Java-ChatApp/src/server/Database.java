package server;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import util.Constants;

public class Database {

    private Connection conn;

    public Database() {
        connect();
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection(
                    Constants.DB_URL,
                    Constants.DB_USER,
                    Constants.DB_PASSWORD
            );
            System.out.println("DB Connected");
        } catch (Exception e) {
            System.out.println("DB Connection Error");
        }
    }

    public void saveMessage(String sender, String receiver, String msg) {
      try {
          String sql = "INSERT INTO messages(sender,receiver,message) VALUES(?,?,?)";
          PreparedStatement ps = conn.prepareStatement(sql);
          ps.setString(1, sender);
          ps.setString(2, receiver);
          ps.setString(3, msg);
          ps.executeUpdate();

          System.out.println("Message Saved");

      } catch (Exception e) {
          System.out.println("DB Insert Error");
          e.printStackTrace();
      }
    }

    public void saveFileMessage(String sender, String receiver, String fileName, byte[] fileData) {
        try {
            // Create uploads directory if missing
            File uploadDir = new File("uploads/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate unique file path
            String filePath = "uploads/" + System.currentTimeMillis() + "_" + fileName;

            // Save file physically
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(fileData);
            fos.close();

            // Insert file info into database
            String sql = "INSERT INTO messages(sender, receiver, file_name, file_path, file_type) VALUES(?,?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, fileName);
            ps.setString(4, filePath);
            ps.setString(5, "FILE");

            ps.executeUpdate();

            System.out.println("File Message Saved");

        } catch (Exception e) {
            System.out.println("DB File Insert Error");
            e.printStackTrace();
        }
    }
}