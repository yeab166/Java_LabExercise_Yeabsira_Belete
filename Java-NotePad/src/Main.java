import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NotepadFrame frame = new NotepadFrame();
            frame.setVisible(true);
        });
    }
}
