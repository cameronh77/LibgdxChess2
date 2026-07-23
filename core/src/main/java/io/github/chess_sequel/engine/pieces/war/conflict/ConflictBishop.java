package io.github.chess_sequel.engine.pieces.war.conflict;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Bishop;

import java.util.ArrayList;

/**
 * Conflict bishop. Slides diagonally like a standard bishop but can skip over exactly
 * one blocking piece per diagonal ray — continuing behind it. The skipped piece is not
 * captured; only pieces the bishop actually lands on are taken.
 */
public class ConflictBishop extends Bishop {

    public ConflictBishop(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "conflict-bishop", ChessClass.CONFLICT);
    }

    @Override
    public String getDescription() {
        return "Moves diagonally like a bishop, but can skip over one piece per diagonal.";
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        if (isBlack != board.getWhiteToMove()) return new ArrayList<>();

        ArrayList<Move> moves = new ArrayList<>();

        moves.addAll(rayMoves(board, 1, 1));
        moves.addAll(rayMoves(board, 1, -1));
        moves.addAll(rayMoves(board, -1, 1));
        moves.addAll(rayMoves(board, -1, -1));

        if (!ignoreCheck) {
            ArrayList<Move> trueMoves = new ArrayList<>();
            for (Move move : moves) {
                if (!board.checkEvaluator(move)) trueMoves.add(move);
            }
            return trueMoves;
        }
        return moves;
    }

    private ArrayList<Move> rayMoves(Board board, int dc, int dr) {
        ArrayList<Move> moves = new ArrayList<>();
        boolean skipped = false;
        for (int offset = 1; ; offset++) {
            int tc = col + dc * offset;
            int tr = row + dr * offset;
            if (tc < 0 || tc >= board.boardX || tr < 0 || tr >= board.boardY) break;
            Piece p = board.getTiles().get(tc).get(tr).getPiece();
            if (p != null) {
                if (p.getIsBlack() != isBlack) {
                    moves.add(new Move(this, tc, tr, board));
                }
                if (skipped) break; // second blocker — full stop
                skipped = true;    // first blocker — skip over it and continue
            } else {
                moves.add(new Move(this, tc, tr, board));
            }
        }
        return moves;
    }
}
