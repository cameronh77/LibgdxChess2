package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * Passive strategy king power: neither side may move the same piece on consecutive turns,
 * unless that side has no other legal moves.
 * Applies symmetrically — both the player and the opponent are constrained.
 */
public class ForcedVariationPassive extends PassiveKingPower {

    // Per-side stacks of last-moved pieces, depth matches minimax undo depth
    private final Deque<Piece> whiteHistory = new ArrayDeque<>();
    private final Deque<Piece> blackHistory = new ArrayDeque<>();

    public ForcedVariationPassive(Piece king) {
        super(king, "ForcedVariation");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        // Skip during check validation to avoid recursion and false checkmate
        if (isCheck) return moves;

        Deque<Piece> history = piece.isBlack() ? blackHistory : whiteHistory;
        if (history.isEmpty() || history.peek() != piece) return moves;

        // This piece moved last. Remove its moves only if another friendly piece can move.
        if (otherPiecesHaveMoves(piece, board)) {
            moves.clear();
        }

        return moves;
    }

    @Override
    public void onLand(Piece piece, int landedX, int landedY, Board board) {
        if (piece.isBlack()) blackHistory.push(piece);
        else whiteHistory.push(piece);
    }

    @Override
    public void onUndoLand(Piece piece, int landedX, int landedY, Board board) {
        Deque<Piece> history = piece.isBlack() ? blackHistory : whiteHistory;
        if (!history.isEmpty()) history.pop();
    }

    private boolean otherPiecesHaveMoves(Piece lastMoved, Board board) {
        for (Piece p : board.getPieces()) {
            if (p == lastMoved || p.isBlack() != lastMoved.isBlack()) continue;
            if (!p.generateMoves(board, false).isEmpty()) return true;
        }
        return false;
    }

    @Override
    public String getName() { return "Forced Variation"; }

    @Override
    public String getIconPath() { return "kingPowers/forced-variation.png"; }

    @Override
    public String getDescription() { return "Neither side can move the same piece twice in a row, unless it has no other legal moves."; }
}
