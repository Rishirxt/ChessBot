package logic;

import model.*;
import bot.*;
import java.util.*;

public class GameManager {
    Board board = new Board();
    Scanner sc = new Scanner(System.in);

    public void start() {
        while (true) {
            board.printBoard();
            System.out.println("Your move (e.g., e2 e4): ");

            Move userMove;
            try {
                userMove = ChessNotation.parseMove(sc.nextLine());
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
                continue;
            }

            List<Move> legalMoves = MoveGenerator.getMoves(board, true);
            boolean isLegal = legalMoves.stream().anyMatch(move ->
                    move.fromRow == userMove.fromRow
                            && move.fromCol == userMove.fromCol
                            && move.toRow == userMove.toRow
                            && move.toCol == userMove.toCol);
            if (!isLegal) {
                System.out.println("That move is not legal.");
                continue;
            }

            board.makeMove(userMove);

            Move botMove = Bot.getBestMove(board);
            if (botMove != null) {
                board.makeMove(botMove);
                System.out.println("Bot moved.");
            } else {
                System.out.println("Bot has no legal moves.");
                return;
            }
        }
    }
}
