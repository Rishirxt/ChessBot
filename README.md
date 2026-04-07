# Java Chess Engine

A fully functional chess application written in Java. This project features a graphical user interface (GUI) built with Java Swing, a comprehensive chess rules engine, and a Minimax-based AI opponent.

## Features

*   **Graphical User Interface**: A clean, interactive chess board built using Java Swing (`ui.ChessUI`). Features include moving pieces via drag-and-drop or click-to-move, and a move history panel.
*   **Chess Rules Engine**: Complete implementation of chess rules including valid piece movements, checkmate, stalemate, and special moves like castling and en passant (`logic.MoveGenerator`).
*   **AI Opponent**: Play against the computer! The engine includes an AI bot using the Minimax algorithm (`bot.Minimax`).
*   **Move History**: Tracks and displays game moves using standard algebraic notation (`logic.MoveHistory`, `logic.ChessNotation`).
*   **Object-Oriented Design**: Cleanly separated architecture:
    *   `model`: Data structures representing the `Board`, `Piece`, and `Move`.
    *   `logic`: Core game mechanics, move generation, and state management (`GameManager`).
    *   `bot`: AI logic.
    *   `ui`: The graphical presentation layer.

## Project Structure

```
chessBot/
├── Main.java              # Application entry point
├── bot/                   # AI logic (Minimax algorithm)
│   ├── Bot.java
│   └── Minimax.java
├── logic/                 # Game rules and state management
│   ├── ChessNotation.java
│   ├── GameManager.java
│   ├── MoveGenerator.java
│   └── MoveHistory.java
├── model/                 # Chess data models
│   ├── Board.java
│   ├── Move.java
│   └── Piece.java
└── ui/                    # Graphical user interface
    └── ChessUI.java
```

## Running the Game

### Prerequisites
*   Java Development Kit (JDK) 8 or higher.

### Compilation & Execution
1.  Navigate to the `chessBot` project root directory.
2.  Compile the Java files:
    ```bash
    javac Main.java bot/*.java logic/*.java model/*.java ui/*.java
    ```
3.  Run the application:
    ```bash
    java Main
    ```

## Development History

This project has been developed iteratively, starting from basic move validation and progressing towards a fully playable game with high-quality Unicode chess symbols, robust checkmate/stalemate detection, and a functional API/bot integration.
