package logic;

import model.Move;

public class ChessNotation {

    public static Move parseMove(String input) {
        String[] parts = input.split(" ");
        String from = parts[0];
        String to = parts[1];

        int fromCol = from.charAt(0) - 'a';
        int fromRow = 8 - (from.charAt(1) - '0');

        int toCol = to.charAt(0) - 'a';
        int toRow = 8 - (to.charAt(1) - '0');

        return new Move(fromRow, fromCol, toRow, toCol);
    }
}