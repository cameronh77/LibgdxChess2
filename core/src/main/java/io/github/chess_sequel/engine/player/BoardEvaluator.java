package io.github.chess_sequel.engine.player;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

/**
 * Static heuristic evaluator for the minimax search. Scores from the bot's perspective
 * (isBlack=true pieces positive, player pieces negative). Includes material value plus
 * small positional bonuses that scale to any board size via normalised coordinates.
 */
public class BoardEvaluator {

    private static final int kingVal   = 10000;
    private static final int queenVal  = 100;
    private static final int castleVal = 80;
    private static final int horseVal  = 75;
    private static final int bishopVal = 50;
    private static final int pawnVal   = 10;

    // Max positional bonus per piece — kept small so material stays dominant
    private static final int centralityMax  = 5;
    private static final int advancementMax = 5;

    public static int evaluatePosition(Board board) {
        int posVal = 0;
        for (Piece piece : board.getPieces()) {
            int material;
            switch (piece.getPieceType()) {
                case KING:   material = kingVal;   break;
                case QUEEN:  material = queenVal;  break;
                case CASTLE: material = castleVal; break;
                case HORSE:  material = horseVal;  break;
                case BISHOP: material = bishopVal; break;
                case PAWN:   material = pawnVal;   break;
                default:     material = 0;         break;
            }
            int positional = centralityBonus(piece, board) + advancementBonus(piece, board);
            posVal += piece.isBlack() ? (material + positional) : -(material + positional);
        }
        return posVal;
    }

    /**
     * Bonus for being close to the board's centre, normalised so it works on any board size.
     * A piece exactly in the centre scores centralityMax; a corner piece scores 0.
     */
    private static int centralityBonus(Piece piece, Board board) {
        double cx = (board.boardX - 1) / 2.0;
        double cy = (board.boardY - 1) / 2.0;
        double maxDist = cx + cy;
        if (maxDist == 0) return 0;
        double dist = Math.abs(piece.getCol() - cx) + Math.abs(piece.getRow() - cy);
        return (int)(centralityMax * (1.0 - dist / maxDist));
    }

    /**
     * Bonus for pawn advancement toward the opponent's back rank.
     * Bot pawns (isBlack=true) advance from high rows to low rows; player pawns go the other way.
     * Normalised by board height so it works across different room sizes.
     */
    private static int advancementBonus(Piece piece, Board board) {
        if (piece.getPieceType() != PieceType.PAWN) return 0;
        if (board.boardY <= 1) return 0;
        double adv = piece.isBlack()
            ? 1.0 - piece.getRow() / (double)(board.boardY - 1)
            :       piece.getRow() / (double)(board.boardY - 1);
        return (int)(advancementMax * adv);
    }
}
