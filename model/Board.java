package model;

public class Board {
    public Piece[][] board = new Piece[8][8];

    public Board() {
        setupBoard();
    }

    public Board(boolean empty) {
        if (!empty) {
            setupBoard();
        }
    }

    public Board clone() {
        Board newBoard = new Board(true);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] != null) {
                    Piece p = new Piece(board[r][c].type, board[r][c].isWhite);
                    p.hasMoved = board[r][c].hasMoved;
                    newBoard.board[r][c] = p;
                }
            }
        }
        return newBoard;
    }

    public void setupBoard() {
        board = new Piece[8][8];

        for (int i = 0; i < 8; i++) {
            board[1][i] = new Piece(Piece.Type.PAWN, false);
            board[6][i] = new Piece(Piece.Type.PAWN, true);
        }

        board[0][0] = board[0][7] = new Piece(Piece.Type.ROOK, false);
        board[7][0] = board[7][7] = new Piece(Piece.Type.ROOK, true);

        board[0][1] = board[0][6] = new Piece(Piece.Type.KNIGHT, false);
        board[7][1] = board[7][6] = new Piece(Piece.Type.KNIGHT, true);

        board[0][2] = board[0][5] = new Piece(Piece.Type.BISHOP, false);
        board[7][2] = board[7][5] = new Piece(Piece.Type.BISHOP, true);

        board[0][3] = new Piece(Piece.Type.QUEEN, false);
        board[7][3] = new Piece(Piece.Type.QUEEN, true);

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
        Piece piece = board[move.fromRow][move.fromCol];
        if (piece == null) {
            return;
        }

        board[move.toRow][move.toCol] = piece;
        board[move.fromRow][move.fromCol] = null;

        if (piece.type == Piece.Type.KING && Math.abs(move.fromCol - move.toCol) == 2) {
            if (move.toCol == 6) {
                board[move.fromRow][5] = board[move.fromRow][7];
                board[move.fromRow][7] = null;
                if (board[move.fromRow][5] != null) {
                    board[move.fromRow][5].hasMoved = true;
                }
            } else if (move.toCol == 2) {
                board[move.fromRow][3] = board[move.fromRow][0];
                board[move.fromRow][0] = null;
                if (board[move.fromRow][3] != null) {
                    board[move.fromRow][3].hasMoved = true;
                }
            }
        }

        if (piece.type == Piece.Type.PAWN && (move.toRow == 0 || move.toRow == 7)) {
            Piece promoted = new Piece(Piece.Type.QUEEN, piece.isWhite);
            promoted.hasMoved = true;
            board[move.toRow][move.toCol] = promoted;
            return;
        }

        piece.hasMoved = true;
    }
}
