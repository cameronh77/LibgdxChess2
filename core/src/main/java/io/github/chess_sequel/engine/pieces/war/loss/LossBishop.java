package io.github.chess_sequel.engine.pieces.war.loss;

import io.github.chess_sequel.engine.auras.WraithAura;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Bishop;

import java.util.ArrayList;

/**
 * Loss faction bishop. Moves diagonally like a standard bishop. After each move it toggles
 * between a normal state and a phased state. While phased it cannot capture and the
 * {@link WraithAura} prevents enemies from targeting its tile.
 */
public class LossBishop extends Bishop {

    private boolean phased = false;
    private Board activeBoard;
    private final WraithAura wraithAura = new WraithAura(this);

    public LossBishop(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "loss-bishop", ChessClass.LOSS);
    }

    @Override
    public String getDescription() { return "Moves diagonally. Alternates each move between normal and phased — while phased it cannot capture or be captured."; }

    public boolean isPhased() { return phased; }

    @Override
    public void onStart(Board board) {
        this.activeBoard = board;
        board.addAura(wraithAura);
    }

    @Override
    public void onCapture(Piece piece) {
        if (activeBoard != null) activeBoard.removeAura(wraithAura);
    }

    @Override
    public void undoOnCapture(Piece piece) {
        if (activeBoard != null) activeBoard.addAura(wraithAura);
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        if (isBlack != board.getWhiteToMove()) return new ArrayList<>();

        ArrayList<Move> moves = super.generateBaseMoves(board, true);
        if (phased) moves.removeIf(m -> m.getCapturedPiece() != null);

        if (!ignoreCheck) {
            ArrayList<Move> trueMoves = new ArrayList<>();
            for (Move move : moves) {
                if (!board.checkEvaluator(move)) trueMoves.add(move);
            }
            return trueMoves;
        }
        return moves;
    }

    @Override
    public void postMove(Move move, Board board) {
        if (move.getMovingPiece() == this) phased = !phased;
    }

    @Override
    public void undoPostMove(Move move, Board board) {
        if (move.getMovingPiece() == this) phased = !phased;
    }
}
