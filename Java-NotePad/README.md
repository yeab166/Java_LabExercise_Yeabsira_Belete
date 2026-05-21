# My Notepad

A complete desktop Notepad application built with Java Swing.

## Project Structure

- `src/Main.java` - Application entry point and main method.
- `src/NotepadFrame.java` - Main UI frame, menus, text area, status bar, themes, and document actions.
- `src/FileManager.java` - File loading and saving utilities.
- `README.md` - This file.

## Features

- Text editing area with scroll support
- File menu: New, Open, Save, Save As, Exit
- Edit menu: Cut, Copy, Paste, Select All
- Format menu: Word Wrap toggle and font size selection
- View menu: Dark Mode toggle
- Help menu with About dialog
- Unsaved changes confirmation before closing or opening a new file
- Status bar with line count and character count
- Open and save `.txt` files using `JFileChooser`

## How to Run in VS Code

1. Open the project folder in VS Code: `MyNotepad`
2. Make sure you have a Java Development Kit (JDK) installed.
3. Install the Java Extension Pack if needed.
4. Open `src/Main.java` and click `Run` or use the VS Code Run menu.

## How to Compile and Run from Command Line

From the project root folder (`MyNotepad`):

```powershell
cd c:\Users\YEABSIRA BELETE\OneDrive\Documents\Codes\Java-NotePad\MyNotepad
javac src\*.java -d out
java -cp out Main
```

If using Windows command prompt or PowerShell, this will compile the source files into the `out` folder and launch the application.

## Notes

- The application uses only standard Java libraries.
- No external dependencies are required.
- The UI is resizable and adapts to window size.

# How to compile and run the code.

 -> javac src\*.java -d out
 
 -> java -cp out Main