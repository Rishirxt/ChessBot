package logic;

import model.*;
import java.util.*;

public class MoveGenerator {

    public static List<Move> getMoves(Board board, boolean isWhite) {
        List<Move> pseudo = getPseudoLegalMoves(board, isWhite);
        List<Move> legal = new ArrayList<>();
        for (Move m : pseudo) {
            Board copy = board.clone();
            copy.makeMove(m);
            if (!inCheck(copy, isWhite)) {
                legal.add(m);
            }
        }
        return legal;
    }

    public static boolean inCheck(Board board, boolean isWhite) {
        int kingR = -1, kingC = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.board[r][c];
                if (p != null && p.type == Piece.Type.KING && p.isWhite == isWhite) {
                    kingR = r;
                    kingC = c;
                    break;
                }
            }
        }
        if (kingR == -1) return false;

        List<Move> enemyMoves = getPseudoLegalMoves(board, !isWhite);
        for (Move m : enemyMoves) {
            if (m.toRow == kingR && m.toCol == kingC) {
                return true;
            }
        }
        return false;
    }

    public static List<Move> getPseudoLegalMoves(Board board, boolean isWhite) {
        List<Move> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.board[r][c];
                if (p == null || p.isWhite != isWhite)
                    continue;

                switch (p.type) {
                    case PAWN:
                        int dir = isWhite ? -1 : 1;
                        int newRow = r + dir;
                        if (isValid(newRow, c) && board.board[newRow][c] == null) {
                            moves.add(new Move(r, c, newRow, c));
                            // double initial move
                            if ((isWhite && r == 6) || (!isWhite && r == 1)) {
                                int doubleRow = r + 2 * dir;
                                if (board.board[doubleRow][c] == null) {
                                    moves.add(new Move(r, c, doubleRow, c));
                                }
                            }
                        }
                        // Captures
                        if (isValid(newRow, c - 1) && board.board[newRow][c - 1] != null && board.board[newRow][c - 1].isWhite != isWhite) {
                            moves.add(new Move(r, c, newRow, c - 1));
                        }
                        if (isValid(newRow, c + 1) && board.board[newRow][c + 1] != null && board.board[newRow][c + 1].isWhite != isWhite) {
                            moves.add(new Move(r, c, newRow, c + 1));
                        }
                        break;

                    case ROOK:
                        addLinearMoves(board, moves, r, c, isWhite,
                                new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } });
                        break;

                    case KNIGHT:
                        int[][] kMoves = { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { 1, -2 }, { -1, 2 },
                                { -1, -2 } };
                        for (int[] m : kMoves) {
                            int nr = r + m[0], nc = c + m[1];
                            if (isValid(nr, nc) && isFreeOrEnemy(board, nr, nc, isWhite)) {
                                moves.add(new Move(r, c, nr, nc));
                            }
                        }
                        break;

                    case BISHOP:
                        addLinearMoves(board, moves, r, c, isWhite,
                                new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } });
                        break;

                    case QUEEN:
                        addLinearMoves(board, moves, r, c, isWhite,
                                new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                                        { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } });
                        break;

                    case KING:
                        int[][] kingDirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
                        for (int[] d : kingDirs) {
                            int nr = r + d[0], nc = c + d[1];
                            if (isValid(nr, nc) && isFreeOrEnemy(board, nr, nc, isWhite)) {
                                moves.add(new Move(r, c, nr, nc));
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        }
        return moves;
    }

    private static void addLinearMoves(Board b, List<Move> moves, int r, int c, boolean isWhite, int[][] dirs) {
        for (int[] d : dirs) {
            int nr = r, nc = c;
            while (true) {
                nr += d[0];
                nc += d[1];
                if (!isValid(nr, nc))
                    break;

                if (b.board[nr][nc] == null) {
                    moves.add(new Move(r, c, nr, nc));
                } else {
                    if (b.board[nr][nc].isWhite != isWhite)
                        moves.add(new Move(r, c, nr, nc));
                    break;
                }
            }
        }
    }

    private static boolean isValid(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    private static boolean isFreeOrEnemy(Board b, int r, int c, boolean isWhite) {
        return b.board[r][c] == null || b.board[r][c].isWhite != isWhite;
    }
}