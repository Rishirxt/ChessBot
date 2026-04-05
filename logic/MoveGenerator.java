package logic;

import model.*;
import java.util.*;

public class MoveGenerator {
    private static final int[][] ROOK_DIRS = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
    private static final int[][] BISHOP_DIRS = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
    private static final int[][] QUEEN_DIRS = {
            { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
            { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
    };
    private static final int[][] KNIGHT_DIRS = {
            { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 },
            { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 }
    };
    private static final int[][] KING_DIRS = {
            { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
            { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
    };

    public static List<Move> getMoves(Board board, boolean isWhite) {
        List<Move> pseudo = getPseudoLegalMoves(board, isWhite);
        List<Move> legal = new ArrayList<>();
        for (Move move : pseudo) {
            if (capturesKing(board, move)) {
                continue;
            }

            Board copy = board.clone();
            copy.makeMove(move);
            if (inCheck(copy, isWhite)) {
                continue;
            }

            Piece movingPiece = board.board[move.fromRow][move.fromCol];
            if (movingPiece != null && movingPiece.type == Piece.Type.KING && Math.abs(move.toCol - move.fromCol) == 2) {
                int crossCol = (move.fromCol + move.toCol) / 2;
                if (isSquareAttacked(board, move.fromRow, crossCol, !isWhite)) {
                    continue;
                }
            }
            legal.add(move);
        }
        return legal;
    }

    public static boolean inCheck(Board board, boolean isWhite) {
        int kingRow = -1;
        int kingCol = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.board[r][c];
                if (piece != null && piece.type == Piece.Type.KING && piece.isWhite == isWhite) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }
        if (kingRow == -1) {
            return true;
        }
        return isSquareAttacked(board, kingRow, kingCol, !isWhite);
    }

    public static List<Move> getPseudoLegalMoves(Board board, boolean isWhite) {
        List<Move> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.board[r][c];
                if (piece == null || piece.isWhite != isWhite) {
                    continue;
                }

                switch (piece.type) {
                    case PAWN -> addPawnMoves(board, moves, r, c, isWhite);
                    case ROOK -> addLinearMoves(board, moves, r, c, isWhite, ROOK_DIRS);
                    case KNIGHT -> addKnightMoves(board, moves, r, c, isWhite);
                    case BISHOP -> addLinearMoves(board, moves, r, c, isWhite, BISHOP_DIRS);
                    case QUEEN -> addLinearMoves(board, moves, r, c, isWhite, QUEEN_DIRS);
                    case KING -> addKingMoves(board, moves, r, c, isWhite, piece);
                    default -> {
                    }
                }
            }
        }
        return moves;
    }

    private static void addPawnMoves(Board board, List<Move> moves, int row, int col, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        int nextRow = row + direction;
        if (isValid(nextRow, col) && board.board[nextRow][col] == null) {
            moves.add(new Move(row, col, nextRow, col));
            int startRow = isWhite ? 6 : 1;
            int doubleRow = row + (2 * direction);
            if (row == startRow && isValid(doubleRow, col) && board.board[doubleRow][col] == null) {
                moves.add(new Move(row, col, doubleRow, col));
            }
        }

        addPawnCapture(board, moves, row, col, nextRow, col - 1, isWhite);
        addPawnCapture(board, moves, row, col, nextRow, col + 1, isWhite);
    }

    private static void addPawnCapture(Board board, List<Move> moves, int fromRow, int fromCol,
            int toRow, int toCol, boolean isWhite) {
        if (isValid(toRow, toCol) && board.board[toRow][toCol] != null && board.board[toRow][toCol].isWhite != isWhite) {
            moves.add(new Move(fromRow, fromCol, toRow, toCol));
        }
    }

    private static void addKnightMoves(Board board, List<Move> moves, int row, int col, boolean isWhite) {
        for (int[] dir : KNIGHT_DIRS) {
            int nextRow = row + dir[0];
            int nextCol = col + dir[1];
            if (isValid(nextRow, nextCol) && isFreeOrEnemy(board, nextRow, nextCol, isWhite)) {
                moves.add(new Move(row, col, nextRow, nextCol));
            }
        }
    }

    private static void addKingMoves(Board board, List<Move> moves, int row, int col, boolean isWhite, Piece king) {
        for (int[] dir : KING_DIRS) {
            int nextRow = row + dir[0];
            int nextCol = col + dir[1];
            if (isValid(nextRow, nextCol) && isFreeOrEnemy(board, nextRow, nextCol, isWhite)) {
                moves.add(new Move(row, col, nextRow, nextCol));
            }
        }

        if (!king.hasMoved && !inCheck(board, isWhite)) {
            if (canCastle(board, row, isWhite, 7, new int[] { 5, 6 })) {
                moves.add(new Move(row, col, row, col + 2));
            }
            if (canCastle(board, row, isWhite, 0, new int[] { 1, 2, 3 })) {
                moves.add(new Move(row, col, row, col - 2));
            }
        }
    }

    private static void addLinearMoves(Board board, List<Move> moves, int row, int col, boolean isWhite, int[][] dirs) {
        for (int[] dir : dirs) {
            int nextRow = row + dir[0];
            int nextCol = col + dir[1];
            while (isValid(nextRow, nextCol)) {
                Piece target = board.board[nextRow][nextCol];
                if (target == null) {
                    moves.add(new Move(row, col, nextRow, nextCol));
                } else {
                    if (target.isWhite != isWhite) {
                        moves.add(new Move(row, col, nextRow, nextCol));
                    }
                    break;
                }
                nextRow += dir[0];
                nextCol += dir[1];
            }
        }
    }

    private static boolean canCastle(Board board, int row, boolean isWhite, int rookCol, int[] emptyCols) {
        Piece rook = board.board[row][rookCol];
        if (rook == null || rook.type != Piece.Type.ROOK || rook.isWhite != isWhite || rook.hasMoved) {
            return false;
        }

        for (int col : emptyCols) {
            if (board.board[row][col] != null) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSquareAttacked(Board board, int targetRow, int targetCol, boolean byWhite) {
        int pawnRow = targetRow + (byWhite ? 1 : -1);
        if (isValid(pawnRow, targetCol - 1) && matchesPiece(board, pawnRow, targetCol - 1, byWhite, Piece.Type.PAWN)) {
            return true;
        }
        if (isValid(pawnRow, targetCol + 1) && matchesPiece(board, pawnRow, targetCol + 1, byWhite, Piece.Type.PAWN)) {
            return true;
        }

        for (int[] dir : KNIGHT_DIRS) {
            int row = targetRow + dir[0];
            int col = targetCol + dir[1];
            if (isValid(row, col) && matchesPiece(board, row, col, byWhite, Piece.Type.KNIGHT)) {
                return true;
            }
        }

        if (isAttackedAlong(board, targetRow, targetCol, byWhite, ROOK_DIRS, Piece.Type.ROOK, Piece.Type.QUEEN)) {
            return true;
        }
        if (isAttackedAlong(board, targetRow, targetCol, byWhite, BISHOP_DIRS, Piece.Type.BISHOP, Piece.Type.QUEEN)) {
            return true;
        }

        for (int[] dir : KING_DIRS) {
            int row = targetRow + dir[0];
            int col = targetCol + dir[1];
            if (isValid(row, col) && matchesPiece(board, row, col, byWhite, Piece.Type.KING)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAttackedAlong(Board board, int targetRow, int targetCol, boolean byWhite,
            int[][] dirs, Piece.Type primary, Piece.Type secondary) {
        for (int[] dir : dirs) {
            int row = targetRow + dir[0];
            int col = targetCol + dir[1];
            while (isValid(row, col)) {
                Piece piece = board.board[row][col];
                if (piece != null) {
                    if (piece.isWhite == byWhite && (piece.type == primary || piece.type == secondary)) {
                        return true;
                    }
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
        return false;
    }

    private static boolean matchesPiece(Board board, int row, int col, boolean isWhite, Piece.Type type) {
        Piece piece = board.board[row][col];
        return piece != null && piece.isWhite == isWhite && piece.type == type;
    }

    private static boolean isValid(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private static boolean isFreeOrEnemy(Board board, int row, int col, boolean isWhite) {
        return board.board[row][col] == null || board.board[row][col].isWhite != isWhite;
    }

    private static boolean capturesKing(Board board, Move move) {
        Piece target = board.board[move.toRow][move.toCol];
        return target != null && target.type == Piece.Type.KING;
    }
}
