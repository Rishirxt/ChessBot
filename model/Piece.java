package model;

public class Piece {
    public enum Type { PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING }

    public Type type;
    public boolean isWhite;

    public Piece(Type type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }

    @Override
    public String toString() {
        switch (type) {
            case PAWN: return isWhite ? "♙" : "♟";
            case ROOK: return isWhite ? "♖" : "♜";
            case KNIGHT: return isWhite ? "♘" : "♞";
            case BISHOP: return isWhite ? "♗" : "♝";
            case QUEEN: return isWhite ? "♕" : "♛";
            case KING: return isWhite ? "♔" : "♚";
            default: return "";
        }
    }
}