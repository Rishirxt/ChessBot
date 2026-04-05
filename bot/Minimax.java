package bot;

import model.*;
import logic.*;
import java.util.*;

public class Minimax {
    private static final int CHECKMATE_SCORE = 100000;

    public static int evaluate(Board board) {
        int score = 0;
        for (Piece[] row : board.board) {
            for (Piece p : row) {
                if (p == null) {
                    continue;
                }
                int val = switch (p.type) {
                    case PAWN -> 10;
                    case KNIGHT, BISHOP -> 30;
                    case ROOK -> 50;
                    case QUEEN -> 90;
                    case KING -> 900;
                };
                score += p.isWhite ? val : -val;
            }
        }
        return score;
    }

    public static int minimax(Board board, int depth, boolean isMax) {
        if (depth == 0) {
            return evaluate(board);
        }

        List<Move> moves = MoveGenerator.getMoves(board, isMax);
        if (moves.isEmpty()) {
            if (MoveGenerator.inCheck(board, isMax)) {
                return isMax ? -CHECKMATE_SCORE - depth : CHECKMATE_SCORE + depth;
            }
            return 0;
        }

        if (isMax) {
            int max = Integer.MIN_VALUE;
            for (Move m : moves) {
                Board copy = board.clone();
                copy.makeMove(m);
                max = Math.max(max, minimax(copy, depth - 1, false));
            }
            return max;
        }

        int min = Integer.MAX_VALUE;
        for (Move m : moves) {
            Board copy = board.clone();
            copy.makeMove(m);
            min = Math.min(min, minimax(copy, depth - 1, true));
        }
        return min;
    }
}
