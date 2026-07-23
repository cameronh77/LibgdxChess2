package io.github.chess_sequel.engine.pieces.war.conflict;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.BerserkerMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.Queen;

import java.util.ArrayList;

/**
 * Conflict queen. Slides in all 8 directions like a standard queen. On landing, every
 * non-king piece in the 3x3 around the destination is captured — including friendlies.
 * The AoE is tracked on the BerserkerMove for undo support.
 */
public class Berserker extends Queen {

    public Berserker(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "berserker", ChessClass.CONFLICT);
    }

    @Override
    public String getDescription() {
        return "Moves like a queen. On landing, captures all non-king pieces in the surrounding 3x3, including friendlies.";
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        if (isBlack != board.getWhiteToMove()) return new ArrayList<>();

        // Re-generate Queen moves but wrap each in a BerserkerMove
        ArrayList<Move> rawMoves = super.generateBaseMoves(board, true);
        ArrayList<Move> berserkerMoves = new ArrayList<>();
        for (Move m : rawMoves) {
            berserkerMoves.add(new BerserkerMove(this, m.getNewX(), m.getNewY(), board));
        }

        if (!ignoreCheck) {
            ArrayList<Move> trueMoves = new ArrayList<>();
            for (Move move : berserkerMoves) {
                if (!board.checkEvaluator(move)) trueMoves.add(move);
            }
            return trueMoves;
        }
        return berserkerMoves;
    }

    @Override
    public void postMove(Move move, Board board) {
        if (move.getMovingPiece() != this) return;
        if (move instanceof BerserkerMove) {
            ((BerserkerMove) move).captureAoe(this, board);
        }
    }

    @Override
    public void undoPostMove(Move move, Board board) {
        if (move.getMovingPiece() != this) return;
        if (move instanceof BerserkerMove) {
            ((BerserkerMove) move).restoreAoe(board, this);
        }
    }
}
