## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## Java Poker Game

This project now includes a Swing-based poker game with a player vs dealer hand.

To compile and run from the workspace root:

1. Open a terminal in the project folder.
2. Compile the game:
   ```powershell
   javac src\*.java
   ```
3. Run the game:
   ```powershell
   java -cp src App
   ```

The game uses 5-card draw rules. Click `Deal`, choose cards to discard, then click `Draw` to see the dealer hand and winner.


# How to Compile and Run

 -> javac src\*.java
 -> java -cp src App