# Java-Projects Repository

This repository contains three Java desktop applications developed in Java SE:

1. **Java-ChatApp**
2. **Java-NotePad**
3. **Java-PockerGame**

---

## Projects Overview

### Java-ChatApp
A client-server chat application built with Java Swing and socket programming.

- `src/server/ServerGUI.java` - Server UI and connection manager.
- `src/server/ClientHandler.java` - Handles individual connected clients.
- `src/server/Database.java` - Database utilities for MySQL usage.
- `src/client/ClientGUI.java` - Chat client UI.
- `src/util/Constants.java` - Shared configuration values.
- `lib/` - External dependencies, including the MySQL JDBC driver.
- `bin/` - Compiled classes.

### Java-NotePad
A desktop text editor built with Java Swing for file editing and document management.

- `src/Main.java` - Application entry point.
- `src/NotepadFrame.java` - Main UI window, menus, and text editor functionality.
- `src/FileManager.java` - File open/save helper methods.
- `bin/` - Compiled classes.

### Java-PockerGame
A desktop Poker game built with Java Swing that demonstrates game logic, card handling, and a player UI.

- `src/` - Source files for the game (UI, game logic, player/deck classes).
- `bin/` - Compiled classes.

---

## Activity Summary

This repository demonstrates:

- GUI application development using Java Swing.
- Network programming with a multi-client chat server.
- File I/O and desktop text editing in a notepad application.
- Project organization with separate package directories for client, server, and utilities.
- Use of external libraries and dependency management for `Java-ChatApp`.

- Implementation of game rules and card/deck management in `Java-PockerGame`.

---

## How to Run

### Java-ChatApp
From the root of `Java-ChatApp`:

```powershell
javac -cp "lib/mysql-connector-j-9.6.0.jar" -d bin src\server\*.java src\client\*.java src\util\*.java
java -cp "bin;lib/mysql-connector-j-9.6.0.jar" server.ServerGUI
java -cp "bin;lib/mysql-connector-j-9.6.0.jar" client.ClientGUI
```

### Java-NotePad
From the root of `Java-NotePad`:

```powershell
javac src\*.java -d out
java -cp out Main
```

### Java-PockerGame
From the root of `Java-PockerGame`:

```powershell
javac src\*.java
java -cp src App
```

---

## Notes

- `Java-ChatApp` requires the MySQL JDBC driver available in `Java-ChatApp/lib/`.
- `Java-NotePad` uses only standard Java libraries and Swing.
- The root of this repository is designed to document both projects together for GitHub.
