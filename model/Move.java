package model;

public class Move {
    public int fromRow, fromCol, toRow, toCol;

    public Move(int fr, int fc, int tr, int tc) {
        this.fromRow = fr;
        this.fromCol = fc;
        this.toRow = tr;
        this.toCol = tc;
    }
}