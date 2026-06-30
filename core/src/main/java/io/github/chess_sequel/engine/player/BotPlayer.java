package io.github.chess_sequel.engine.player;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.jsonTypes.PiecePlacement;
import io.github.chess_sequel.engine.jsonTypes.Rewards;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.*;
import io.github.chess_sequel.engine.pieces.factories.PieceFactory;

import java.util.ArrayList;

public class BotPlayer extends Player{

    private int skillLevel;
    private ArrayList<PiecePlacement> army;
    private boolean defeated = false;
    private GameRun gameRun;
    private Rewards rewards;

    public BotPlayer(GameRun gameRun, int skillLevel, ArrayList<PiecePlacement> army, Rewards rewards){
        this.skillLevel = skillLevel;
        this.army = army;
        this.createPieceList();
        this.gameRun = gameRun;
        this.rewards = rewards;
    }

    @Override
    public void takeTurn(Board board){
        Move move = findBestMove(board, skillLevel);
        if(move != null){
            move.execute();
        }
        else{
            this.defeated = true;
            if(board instanceof MatchBoard){
                ((MatchBoard) board).clearEndMatchEffect();
            }
            gameRun.popBoard();
            gameRun.setPendingRewards(rewards);
            gameRun.setGameState(GameState.MATCH_WON);
            System.out.println("Bot player has no moves left");
        }

    }

    public void onLeaderCaptured(Board board) {
        this.defeated = true;
        if (board instanceof MatchBoard) {
            ((MatchBoard) board).clearEndMatchEffect();
        }
        gameRun.popBoard();
        gameRun.setPendingRewards(rewards);
        gameRun.setGameState(GameState.MATCH_WON);
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
            System.out.println("This is a move: x "+move.getNewX() + "  y  " + move.getNewY());
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
            if(moves.size()==0){
                return BoardEvaluator.evaluatePosition(board, board.getWhiteToMove());
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
            if(moves.size()==0){
                return BoardEvaluator.evaluatePosition(board, board.getWhiteToMove());
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

    @Override
    public void createPieceList(){
        pieces.clear();
        for(PiecePlacement placement: army){
            Piece piece = PieceFactory.generatePiece(placement.piece, placement.x, placement.y, false);
            if(leadPiece == null){
                leadPiece = piece;
            }
            pieces.add(piece);
        }
    }

    public boolean getDefeated(){
        return defeated;
    }



}
