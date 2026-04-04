package ui;

import bot.*;

import model.*;
import logic.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessUI extends JFrame {

    private JButton[][] buttons = new JButton[8][8];
    private Board board = new Board();

    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean botThinking = false;

    public ChessUI() {
        setTitle("Chess Bot");
        setSize(600, 600);
        setLayout(new GridLayout(8, 8));

        initializeBoard();
        refreshBoard();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initializeBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton btn = new JButton();
                buttons[r][c] = btn;

                btn.setFont(new Font("SansSerif", Font.PLAIN, 40));
                btn.setMargin(new Insets(0, 0, 0, 0));

                int row = r;
                int col = c;

                btn.addActionListener(e -> handleClick(row, col));

                // alternating colors
                if ((r + c) % 2 == 0)
                    btn.setBackground(Color.WHITE);
                else
                    btn.setBackground(new Color(118, 150, 86));

                add(btn);
            }
        }
    }

    private boolean checkGameOver(boolean isWhite) {
        java.util.List<Move> legalMoves = MoveGenerator.getMoves(board, isWhite);
        if (legalMoves.isEmpty()) {
            boolean inCheck = MoveGenerator.inCheck(board, isWhite);
            String message = inCheck ? "Checkmate! " + (isWhite ? "Black" : "White") + " wins." : "Stalemate!";
            JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    private void handleClick(int row, int col) {
        if (botThinking)
            return;

        if (selectedRow == -1) {
            // select piece
            if (board.board[row][col] != null &&
                    board.board[row][col].isWhite) {

                selectedRow = row;
                selectedCol = col;
            }
        } else {
            // If they clicked another white piece, change selection
            if (board.board[row][col] != null && board.board[row][col].isWhite) {
                selectedRow = row;
                selectedCol = col;
                return;
            }

            // check if move is valid
            java.util.List<Move> validMoves = MoveGenerator.getMoves(board, true);
            boolean valid = false;
            for (Move m : validMoves) {
                if (m.fromRow == selectedRow && m.fromCol == selectedCol && m.toRow == row && m.toCol == col) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                // move piece
                Move move = new Move(selectedRow, selectedCol, row, col);
                board.makeMove(move);

                selectedRow = -1;
                selectedCol = -1;

                refreshBoard();

                if (checkGameOver(false))
                    return;

                botThinking = true;
                Timer timer = new Timer(2500, e -> {
                    // BOT MOVE
                    Move botMove = Bot.getBestMove(board);
                    if (botMove != null) {
                        board.makeMove(botMove);
                    }

                    refreshBoard();
                    botThinking = false;

                    checkGameOver(true);
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                // invalid move, reset selection
                selectedRow = -1;
                selectedCol = -1;
            }
        }
    }

    private void refreshBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.board[r][c];
                buttons[r][c].setText(p == null ? "" : p.toString());
                if (p != null) {
                    buttons[r][c].setForeground(p.isWhite ? Color.BLUE : Color.RED);
                }
            }
        }
    }
}