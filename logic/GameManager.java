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
            String input = sc.nextLine();
            Move userMove = ChessNotation.parseMove(input);
            board.makeMove(userMove);

            Move botMove = Bot.getBestMove(board);
            if (botMove != null) {
                board.makeMove(botMove);
                System.out.println("Bot moved.");
            }
        }
    }
}