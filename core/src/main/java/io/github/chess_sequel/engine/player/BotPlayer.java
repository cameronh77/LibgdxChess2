package io.github.chess_sequel.engine.player;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.jsonTypes.Rewards;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.*;

import java.util.ArrayList;

public class BotPlayer extends Player{

    private int skillLevel;
    private String army;
    private boolean defeated = false;
    private GameRun gameRun;
    private Rewards rewards;

    public BotPlayer(GameRun gameRun, int skillLevel, String army, Rewards rewards){
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
        } else{
            this.defeated = true;
            gameRun.popBoard();
            if(rewards != null){
                gameRun.handleRewards(rewards);
            }
            System.out.println("Bot player has no moves left");
        }

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

    @Override
    public void createPieceList(){

        pieces.clear();
        String[] parts = army.split(" ");

        for(String part: parts){
            System.out.println(part);
            switch(part.charAt(0)){
                case ('p'):
                    Piece pawn = new Pawn(Character.getNumericValue(part.charAt(1)), Character.getNumericValue(part.charAt(2)), true);
                    if(leadPiece == null){
                        leadPiece = pawn;
                    }
                    pieces.add(pawn);
                    break;
                case ('k'):
                    Piece king = new King(Character.getNumericValue(part.charAt(1)), Character.getNumericValue(part.charAt(2)), true);
                    if(leadPiece == null){
                        leadPiece = king;
                    }
                    pieces.add(king);
                    break;
                case ('c'):
                    Piece castle = new Castle(Character.getNumericValue(part.charAt(1)), Character.getNumericValue(part.charAt(2)), true);
                    if(leadPiece == null){
                        leadPiece = castle;
                    }
                    pieces.add(castle);
                    break;
                case ('h'):
                    Piece horse = new Horse(Character.getNumericValue(part.charAt(1)), Character.getNumericValue(part.charAt(2)), true);
                    if(leadPiece == null){
                        leadPiece = horse;
                    }
                    pieces.add(horse);
                    break;
                case ('b'):
                    Piece bishop = new Bishop(Character.getNumericValue(part.charAt(1)), Character.getNumericValue(part.charAt(2)), true);
                    if(leadPiece == null){
                        leadPiece = bishop;
                    }
                    pieces.add(bishop);
                    break;
                case ('q'):
                    Piece queen = new Queen(Character.getNumericValue(part.charAt(1)), Character.getNumericValue(part.charAt(2)), true);
                    if(leadPiece == null){
                        leadPiece = queen;
                    }
                    pieces.add(queen);
                    break;
            }
        }
        /**
        pieces.add(new Pawn(0, 6, true));
        pieces.add(new Pawn(1, 6, true));
        pieces.add(new Pawn(2, 6, true));
        pieces.add(new Pawn(3, 6, true));
        pieces.add(new Pawn(4, 6, true));
        pieces.add(new Pawn(5, 6, true));
        pieces.add(new Pawn(6, 6, true));
        pieces.add(new Pawn(7, 6, true));

        pieces.add(new Castle(0, 7, true));
        pieces.add(new Horse(1, 7, true));
        pieces.add(new Bishop(2, 7, true));
        pieces.add(new Queen(3, 7, true));
        leadPiece = new King(4, 7, true);
        pieces.add(leadPiece);
        pieces.add(new Bishop(5, 7, true));
        pieces.add(new Horse(6, 7, true));
        pieces.add(new Castle(7, 7, true));
         */
    }

    public boolean getDefeated(){
        return defeated;
    }



}
