package logic;

import model.Board;
import model.Move;
import model.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the full history of board states and moves for replay/navigation.
 * Add to the logic/ package alongside MoveGenerator, ChessNotation,
 * GameManager.
 */
public class MoveHistory {

    public static class HistoryEntry {
        public final Board boardSnapshot;
        public final Move move; // the move that LED to this state
        public final String notation; // algebraic notation string e.g. "e2-e4"
        public final boolean wasWhite; // who made this move

        public HistoryEntry(Board snapshot, Move move, String notation, boolean wasWhite) {
            this.boardSnapshot = snapshot;
            this.move = move;
            this.notation = notation;
            this.wasWhite = wasWhite;
        }
    }

    private final List<HistoryEntry> history = new ArrayList<>();
    private final Board initialBoard;

    public MoveHistory(Board initialBoard) {
        this.initialBoard = initialBoard.clone();
    }

    /**
     * Call this after every makeMove() to record the resulting state.
     */
    public void record(Board boardAfterMove, Move move, boolean wasWhite) {
        String notation = toAlgebraic(move);
        history.add(new HistoryEntry(boardAfterMove.clone(), move, notation, wasWhite));
    }

    public int size() {
        return history.size();
    }

    public HistoryEntry get(int index) {
        return history.get(index);
    }

    /**
     * Returns the board at a given history index.
     * Pass -1 to get the initial position.
     */
    public Board getBoardAt(int index) {
        if (index < 0)
            return initialBoard.clone();
        return history.get(index).boardSnapshot.clone();
    }

    /** Returns a list of notation strings like ["e2-e4", "e7-e5", ...] */
    public List<String> getNotations() {
        List<String> list = new ArrayList<>();
        for (HistoryEntry e : history) {
            list.add(e.notation);
        }
        return list;
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    /** Simple coordinate notation, e.g. e2-e4 */
    private String toAlgebraic(Move m) {
        String from = "" + (char) ('a' + m.fromCol) + (8 - m.fromRow);
        String to = "" + (char) ('a' + m.toCol) + (8 - m.toRow);
        return from + "-" + to;
    }
}