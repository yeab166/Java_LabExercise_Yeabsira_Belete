import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class NotepadFrame extends JFrame {
    private final JTextArea textArea;
    private final JLabel statusLabel;
    private final JFileChooser fileChooser;
    private File currentFile;
    private boolean isModified;
    private boolean wordWrapEnabled;
    private boolean darkModeEnabled;

    public NotepadFrame() {
        setTitle("My Notepad");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        textArea = createTextArea();
        statusLabel = createStatusBar();
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Documents (*.txt)", "txt"));

        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        setJMenuBar(buildMenuBar());
        updateStatus();
        applyTheme();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onDocumentChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onDocumentChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onDocumentChanged();
            }
        });
        return area;
    }

    private JLabel createStatusBar() {
        JLabel label = new JLabel("Lines: 1  |  Characters: 0");
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return label;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createFormatMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createHelpMenu());

        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem newItem = createMenuItem("New", KeyEvent.VK_N, e -> newFile());
        JMenuItem openItem = createMenuItem("Open...", KeyEvent.VK_O, e -> openFile());
        JMenuItem saveItem = createMenuItem("Save", KeyEvent.VK_S, e -> saveFile());
        JMenuItem saveAsItem = createMenuItem("Save As...", 0, e -> saveFileAs());
        JMenuItem exitItem = createMenuItem("Exit", KeyEvent.VK_Q, e -> exitApplication());

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");

        JMenuItem cutItem = createMenuItem("Cut", KeyEvent.VK_X, e -> textArea.cut());
        JMenuItem copyItem = createMenuItem("Copy", KeyEvent.VK_C, e -> textArea.copy());
        JMenuItem pasteItem = createMenuItem("Paste", KeyEvent.VK_V, e -> textArea.paste());
        JMenuItem selectAllItem = createMenuItem("Select All", KeyEvent.VK_A, e -> textArea.selectAll());

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);

        return editMenu;
    }

    private JMenu createFormatMenu() {
        JMenu formatMenu = new JMenu("Format");

        JCheckBoxMenuItem wrapToggle = new JCheckBoxMenuItem("Word Wrap");
        wrapToggle.setSelected(true);
        wordWrapEnabled = true;
        wrapToggle.addActionListener(e -> toggleWordWrap(wrapToggle.isSelected()));

        JMenu fontMenu = new JMenu("Font Size");
        int[] sizes = {12, 14, 16, 18, 20, 22, 24};
        for (int size : sizes) {
            JMenuItem sizeItem = new JMenuItem(size + " pt");
            sizeItem.addActionListener(e -> changeFontSize(size));
            fontMenu.add(sizeItem);
        }

        formatMenu.add(wrapToggle);
        formatMenu.addSeparator();
        formatMenu.add(fontMenu);

        return formatMenu;
    }

    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem darkModeToggle = new JCheckBoxMenuItem("Dark Mode");
        darkModeToggle.addActionListener(e -> toggleDarkMode(darkModeToggle.isSelected()));
        viewMenu.add(darkModeToggle);
        return viewMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        return helpMenu;
    }

    private JMenuItem createMenuItem(String text, int keyEvent, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        if (keyEvent != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(keyEvent, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        }
        item.addActionListener(action);
        return item;
    }

    private void onDocumentChanged() {
        isModified = true;
        updateStatus();
        updateTitle();
    }

    private void updateStatus() {
        String content = textArea.getText();
        int charCount = content.length();
        int lineCount = Math.max(1, textArea.getLineCount());
        statusLabel.setText(String.format("Lines: %d  |  Characters: %d", lineCount, charCount));
    }

    private void updateTitle() {
        String fileName = currentFile != null ? currentFile.getName() : "Untitled";
        String modifiedMark = isModified ? "*" : "";
        setTitle(String.format("My Notepad - %s%s", fileName, modifiedMark));
    }

    private void newFile() {
        if (!confirmDiscardChanges()) {
            return;
        }
        textArea.setText("");
        currentFile = null;
        isModified = false;
        updateTitle();
        updateStatus();
    }

    private void openFile() {
        if (!confirmDiscardChanges()) {
            return;
        }
        int choice = fileChooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String content = FileManager.loadFile(file);
                textArea.setText(content);
                currentFile = file;
                isModified = false;
                updateTitle();
                updateStatus();
            } catch (IOException ex) {
                showError("Unable to open file: " + ex.getMessage());
            }
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            saveFileAs();
            return;
        }
        writeFile(currentFile);
    }

    private void saveFileAs() {
        int choice = fileChooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File chosenFile = fileChooser.getSelectedFile();
            if (!chosenFile.getName().toLowerCase().endsWith(".txt")) {
                chosenFile = new File(chosenFile.getParentFile(), chosenFile.getName() + ".txt");
            }
            currentFile = chosenFile;
            writeFile(currentFile);
        }
    }

    private void writeFile(File file) {
        try {
            FileManager.saveFile(file, textArea.getText());
            isModified = false;
            updateTitle();
            showStatusMessage("Saved " + file.getName());
        } catch (IOException ex) {
            showError("Unable to save file: " + ex.getMessage());
        }
    }

    private boolean confirmDiscardChanges() {
        if (!isModified) {
            return true;
        }
        int result = JOptionPane.showConfirmDialog(
                this,
                "You have unsaved changes. Do you want to save before continuing?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            return false;
        }

        if (result == JOptionPane.YES_OPTION) {
            saveFile();
            return !isModified;
        }
        return true;
    }

    private void exitApplication() {
        if (!confirmDiscardChanges()) {
            return;
        }
        dispose();
        System.exit(0);
    }

    private void toggleWordWrap(boolean enabled) {
        wordWrapEnabled = enabled;
        textArea.setLineWrap(enabled);
        textArea.setWrapStyleWord(enabled);
    }

    private void changeFontSize(int size) {
        Font font = textArea.getFont();
        textArea.setFont(new Font(font.getFamily(), font.getStyle(), size));
    }

    private void toggleDarkMode(boolean enabled) {
        darkModeEnabled = enabled;
        applyTheme();
    }

    private void applyTheme() {
        if (darkModeEnabled) {
            textArea.setBackground(new Color(34, 34, 34));
            textArea.setForeground(Color.WHITE);
            textArea.setCaretColor(Color.WHITE);
            statusLabel.setBackground(new Color(45, 45, 45));
            statusLabel.setForeground(Color.LIGHT_GRAY);
            statusLabel.setOpaque(true);
            getContentPane().setBackground(new Color(45, 45, 45));
            setMenuBarColors(new Color(60, 60, 60), Color.WHITE);
        } else {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
            textArea.setCaretColor(Color.BLACK);
            statusLabel.setBackground(new Color(240, 240, 240));
            statusLabel.setForeground(Color.DARK_GRAY);
            statusLabel.setOpaque(true);
            getContentPane().setBackground(null);
            setMenuBarColors(null, null);
        }
    }

    private void setMenuBarColors(Color background, Color foreground) {
        JMenuBar menuBar = getJMenuBar();
        if (menuBar == null) {
            return;
        }
        for (Component comp : menuBar.getComponents()) {
            if (comp instanceof JMenu) {
                JMenu menu = (JMenu) comp;
                menu.setOpaque(background != null);
                if (background != null) {
                    menu.setBackground(background);
                }
                if (foreground != null) {
                    menu.setForeground(foreground);
                }
                for (Component menuItem : menu.getMenuComponents()) {
                    if (menuItem instanceof JMenuItem) {
                        JMenuItem item = (JMenuItem) menuItem;
                        item.setOpaque(background != null);
                        if (background != null) {
                            item.setBackground(background);
                        }
                        if (foreground != null) {
                            item.setForeground(foreground);
                        }
                    }
                }
            }
        }
    }

    private void showAboutDialog() {
        String message = "My Notepad\n" +
                "A lightweight Java Swing text editor.\n" +
                "Built with standard Java libraries and a clean interface.";
        JOptionPane.showMessageDialog(this, message, "About My Notepad", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showStatusMessage(String message) {
        statusLabel.setText(message);
    }
}
