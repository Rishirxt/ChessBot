package logic;

import model.Move;

public class ChessNotation {

    public static Move parseMove(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Move cannot be empty.");
        }

        String[] parts = input.trim().toLowerCase().split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Use moves like 'e2 e4'.");
        }

        String from = parts[0];
        String to = parts[1];
        if (!isSquare(from) || !isSquare(to)) {
            throw new IllegalArgumentException("Squares must be between a1 and h8.");
        }

        int fromCol = from.charAt(0) - 'a';
        int fromRow = 8 - (from.charAt(1) - '0');

        int toCol = to.charAt(0) - 'a';
        int toRow = 8 - (to.charAt(1) - '0');

        return new Move(fromRow, fromCol, toRow, toCol);
    }

    private static boolean isSquare(String square) {
        return square.length() == 2
                && square.charAt(0) >= 'a' && square.charAt(0) <= 'h'
                && square.charAt(1) >= '1' && square.charAt(1) <= '8';
    }
}
