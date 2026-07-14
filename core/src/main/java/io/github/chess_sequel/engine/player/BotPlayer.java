package io.github.chess_sequel.engine.player;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.jsonTypes.PiecePlacement;
import io.github.chess_sequel.engine.jsonTypes.Rewards;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.TurnCondition;
import io.github.chess_sequel.engine.pieces.*;
import io.github.chess_sequel.engine.pieces.factories.PieceFactory;

import java.util.ArrayList;

/**
 * AI-controlled opponent. Selects moves using alpha-beta minimax at a configurable depth.
 * When no moves remain (or the king is captured) the bot sets itself as defeated and triggers
 * the reward and board-pop sequence via {@link io.github.chess_sequel.engine.GameRun}.
 */
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
            if (!move.endsTurn()) {
                takeTurn(board);
            } else {
                TurnCondition condition = board.getTurnCondition();
                if (condition != null) {
                    if (board.hasFrenzyEligibleMoves()) {
                        takeTurn(board);
                    } else {
                        board.setTurnCondition(null);
                    }
                }
            }
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

    /** Called externally when the bot's king piece is captured mid-turn to trigger defeat handling. */
    public void onLeaderCaptured(Board board) {
        this.defeated = true;
        if (board instanceof MatchBoard) {
            ((MatchBoard) board).clearEndMatchEffect();
        }
        gameRun.popBoard();
        gameRun.setPendingRewards(rewards);
        gameRun.setGameState(GameState.MATCH_WON);
    }

    /** Finds the highest-value move for the current side using alpha-beta minimax at the given depth. */
    public static Move findBestMove(Board board, int depth) {
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        ArrayList<Move> moves = new ArrayList<>();
        ArrayList<Piece> pieces = new ArrayList<>();
        pieces.addAll(board.getPieces());
        for(Piece piece: pieces) {
            moves.addAll(piece.generateMoves(board, false));
        }
        capturesFirst(moves);

        for (Move move : moves) {
            boolean wasWhiteToMove = board.getWhiteToMove();
            move.execute();
            boolean turnFlipped = board.getWhiteToMove() != wasWhiteToMove;
            int moveVal = minimax(board, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !turnFlipped);
            move.undo();

            if (moveVal > bestValue) {
                bestValue = moveVal;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * Alpha-beta minimax search. Returns a heuristic board evaluation score from the perspective
     * of the maximising player. Cuts off when alpha >= beta.
     */
    public static int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

        // Base case – leaf node or game over
        if (depth == 0) {
            return BoardEvaluator.evaluatePosition(board);
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
                return BoardEvaluator.evaluatePosition(board);
            }
            capturesFirst(moves);
            for (Move move : moves) {
                boolean wasWhiteToMove = board.getWhiteToMove();
                move.execute();
                boolean turnFlipped = board.getWhiteToMove() != wasWhiteToMove;
                int eval = minimax(board, depth - 1, alpha, beta, turnFlipped ? !maximizingPlayer : maximizingPlayer);
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
                return BoardEvaluator.evaluatePosition(board);
            }
            capturesFirst(moves);
            for (Move move : moves) {
                boolean wasWhiteToMove = board.getWhiteToMove();
                move.execute();
                boolean turnFlipped = board.getWhiteToMove() != wasWhiteToMove;
                int eval = minimax(board, depth - 1, alpha, beta, turnFlipped ? !maximizingPlayer : maximizingPlayer);
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

    private static void capturesFirst(ArrayList<Move> moves) {
        moves.sort((a, b) -> Boolean.compare(b.getCapturedPiece() != null, a.getCapturedPiece() != null));
    }

}
