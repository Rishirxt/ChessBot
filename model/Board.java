package model;

public class Board {
    public Piece[][] board = new Piece[8][8];

    public Board() {
        setupBoard();
    }

    public Board(boolean empty) {
        if (!empty) setupBoard();
    }

    public Board clone() {
        Board newBoard = new Board(true);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] != null) {
                    newBoard.board[r][c] = new Piece(board[r][c].type, board[r][c].isWhite);
                }
            }
        }
        return newBoard;
    }

    public void setupBoard() {
        // Pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Piece(Piece.Type.PAWN, false);
            board[6][i] = new Piece(Piece.Type.PAWN, true);
        }

        // Rooks
        board[0][0] = board[0][7] = new Piece(Piece.Type.ROOK, false);
        board[7][0] = board[7][7] = new Piece(Piece.Type.ROOK, true);

        // Knights
        board[0][1] = board[0][6] = new Piece(Piece.Type.KNIGHT, false);
        board[7][1] = board[7][6] = new Piece(Piece.Type.KNIGHT, true);

        // Bishops
        board[0][2] = board[0][5] = new Piece(Piece.Type.BISHOP, false);
        board[7][2] = board[7][5] = new Piece(Piece.Type.BISHOP, true);

        // Queens
        board[0][3] = new Piece(Piece.Type.QUEEN, false);
        board[7][3] = new Piece(Piece.Type.QUEEN, true);

        // Kings
        board[0][4] = new Piece(Piece.Type.KING, false);
        board[7][4] = new Piece(Piece.Type.KING, true);
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print((board[i][j] == null ? "." : board[i][j]) + " ");
            }
            System.out.println();
        }
    }

    public void makeMove(Move move) {
        board[move.toRow][move.toCol] = board[move.fromRow][move.fromCol];
        board[move.fromRow][move.fromCol] = null;
    }
}