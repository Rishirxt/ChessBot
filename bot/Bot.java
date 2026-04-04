package bot;

import model.*;
import logic.*;
import java.util.*;

public class Bot {

    public static Move getBestMove(Board board) {
        List<Move> moves = MoveGenerator.getMoves(board, false);
        Move best = null;
        int bestVal = Integer.MAX_VALUE;

        for (Move m : moves) {
            Board copy = board.clone();
            copy.makeMove(m);
            int val = Minimax.minimax(copy, 2, true);

            if (val < bestVal) {
                bestVal = val;
                best = m;
            }
        }
        return best;
    }
}