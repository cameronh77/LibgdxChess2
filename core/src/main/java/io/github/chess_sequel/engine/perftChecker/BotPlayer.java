package io.github.chess_sequel.engine.perftChecker;

import io.github.chess_sequel.engine.location.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

public class BotPlayer {

    private int skillLevel;
    private Board board;

    public BotPlayer(int skillLevel, Board board){
        this.skillLevel = skillLevel;
        this.board = board;
    }

    public void takeTurn(){
        Move move = findBestMove(board, skillLevel);
        move.execute();
    }

    public static Move findBestMove(Board board, int depth) {
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        ArrayList<Move> moves = new ArrayList<>();
        ArrayList<Piece> pieces = new ArrayList<>();
        pieces.addAll(board.getPieces());
        for(Piece piece: pieces) {
            moves.addAll(piece.generateMoves(board, false));
        }

        for (Move move : moves) {

            move.execute();
            int moveVal = minimax(board, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            move.undo();

            if (moveVal > bestValue) {
                bestValue = moveVal;
                bestMove = move;
            }
        }

        return bestMove;
    }

    public static int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

        // Base case – leaf node or game over
        if (depth == 0) {
            return BoardEvaluator.evaluatePosition(board, board.getWhiteToMove());
        }

        // MAXIMIZING PLAYER (white)
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            ArrayList<Move> moves = new ArrayList<>();
            ArrayList<Piece> pieces = new ArrayList<>();
            pieces.addAll(board.getPieces());
            for(Piece piece: pieces) {
                moves.addAll(piece.generateMoves(board, false));
            }
            for (Move move : moves) {

                move.execute();
                int eval = minimax(board, depth - 1, alpha, beta, false);
                move.undo();

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha)
                    break; // β cut-off
            }

            return maxEval;
        }

        // MINIMIZING PLAYER (black)
        else {
            int minEval = Integer.MAX_VALUE;

            ArrayList<Move> moves = new ArrayList<>();
            ArrayList<Piece> pieces = new ArrayList<>();
            pieces.addAll(board.getPieces());
            for(Piece piece: pieces) {
                moves.addAll(piece.generateMoves(board, false));
            }
            for (Move move : moves) {

                move.execute();
                int eval = minimax(board, depth - 1, alpha, beta, true);
                move.undo();

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if (beta <= alpha)
                    break; // α cut-off
            }

            return minEval;
        }
    }

}
