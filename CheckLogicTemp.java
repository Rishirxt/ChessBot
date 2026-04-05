import logic.MoveGenerator;
import model.Board;
import model.Move;
import model.Piece;

public class CheckLogicTemp {
    public static void main(String[] args) {
        Board board = new Board(true);
        board.board[7][4] = new Piece(Piece.Type.KING, true);
        board.board[6][4] = new Piece(Piece.Type.QUEEN, true);
        board.board[5][4] = new Piece(Piece.Type.KING, false);

        boolean canCaptureKing = MoveGenerator.getMoves(board, true).stream()
                .anyMatch(m -> m.toRow == 5 && m.toCol == 4);
        if (canCaptureKing) {
            throw new IllegalStateException("A legal move should never capture the enemy king.");
        }

        if (!MoveGenerator.inCheck(board, false)) {
            throw new IllegalStateException("Black king should be detected as in check.");
        }

        System.out.println("Check logic passed");
    }
}