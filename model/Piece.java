package model;

public class Piece {
    public enum Type { PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING }

    public Type type;
    public boolean isWhite;
    public boolean hasMoved = false;

    public Piece(Type type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }

    public String getSymbol() {
        return switch (type) {
            case PAWN -> isWhite ? "\u2659" : "\u265F";
            case ROOK -> isWhite ? "\u2656" : "\u265C";
            case KNIGHT -> isWhite ? "\u2658" : "\u265E";
            case BISHOP -> isWhite ? "\u2657" : "\u265D";
            case QUEEN -> isWhite ? "\u2655" : "\u265B";
            case KING -> isWhite ? "\u2654" : "\u265A";
        };
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}
