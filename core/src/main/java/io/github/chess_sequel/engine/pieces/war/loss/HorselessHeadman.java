package io.github.chess_sequel.engine.pieces.war.loss;

import io.github.chess_sequel.engine.auras.PetrifyingAura;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Horse;

import java.util.ArrayList;

/**
 * Loss faction knight. Jumps in the standard L-shape but only generates moves when at least
 * one friendly piece occupies an adjacent tile — isolated, it cannot move at all. Emits a
 * {@link PetrifyingAura} on every surrounding tile, preventing enemy pieces from moving
 * through them.
 */
public class HorselessHeadman extends Horse {

    private Board activeBoard;

    public HorselessHeadman(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "horseless-headman", ChessClass.LOSS);
    }

    @Override
    public String getDescription() { return "Jumps in an L-shape, but only when adjacent to a friendly piece. Petrifies all surrounding tiles."; }

    @Override
    public void onStart(Board board) {
        this.activeBoard = board;
        addAurasAround(board, col, row);
    }

    @Override
    public void onCapture(Piece piece) {
        if (activeBoard != null) removeAurasAround(activeBoard, col, row);
    }

    @Override
    public void undoOnCapture(Piece piece) {
        if (activeBoard != null) addAurasAround(activeBoard, col, row);
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        if (!hasAdjacentFriendly(board)) return new ArrayList<>();
        return super.generateBaseMoves(board, ignoreCheck);
    }

    @Override
    public void postMove(Move move, Board board) {
        if (move.getMovingPiece() != this) return;
        removeAurasAround(board, move.getOldX(), move.getOldY());
        addAurasAround(board, col, row);
    }

    @Override
    public void undoPostMove(Move move, Board board) {
        if (move.getMovingPiece() != this) return;
        removeAurasAround(board, move.getNewX(), move.getNewY());
        addAurasAround(board, col, row);
    }

    private boolean hasAdjacentFriendly(Board board) {
        for (int dc = -1; dc <= 1; dc++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (dc == 0 && dr == 0) continue;
                int nc = col + dc, nr = row + dr;
                if (nc < 0 || nc >= board.boardX || nr < 0 || nr >= board.boardY) continue;
                Piece p = board.getTiles().get(nc).get(nr).getPiece();
                if (p != null && p.isBlack() == isBlack) return true;
            }
        }
        return false;
    }

    private void addAurasAround(Board b, int c, int r) {
        if (c - 1 >= 0) {
            b.getTiles().get(c-1).get(r).getAuras().add(new PetrifyingAura(this));
            if (r - 1 >= 0)       b.getTiles().get(c-1).get(r-1).getAuras().add(new PetrifyingAura(this));
            if (r + 1 < b.boardY) b.getTiles().get(c-1).get(r+1).getAuras().add(new PetrifyingAura(this));
        }
        if (c + 1 < b.boardX) {
            b.getTiles().get(c+1).get(r).getAuras().add(new PetrifyingAura(this));
            if (r - 1 >= 0)       b.getTiles().get(c+1).get(r-1).getAuras().add(new PetrifyingAura(this));
            if (r + 1 < b.boardY) b.getTiles().get(c+1).get(r+1).getAuras().add(new PetrifyingAura(this));
        }
        if (r - 1 >= 0)       b.getTiles().get(c).get(r-1).getAuras().add(new PetrifyingAura(this));
        if (r + 1 < b.boardY) b.getTiles().get(c).get(r+1).getAuras().add(new PetrifyingAura(this));
    }

    private void removeAurasAround(Board b, int c, int r) {
        if (c - 1 >= 0) {
            b.getTiles().get(c-1).get(r).removeAura(this, "petrifyingAura");
            if (r - 1 >= 0)       b.getTiles().get(c-1).get(r-1).removeAura(this, "petrifyingAura");
            if (r + 1 < b.boardY) b.getTiles().get(c-1).get(r+1).removeAura(this, "petrifyingAura");
        }
        if (c + 1 < b.boardX) {
            b.getTiles().get(c+1).get(r).removeAura(this, "petrifyingAura");
            if (r - 1 >= 0)       b.getTiles().get(c+1).get(r-1).removeAura(this, "petrifyingAura");
            if (r + 1 < b.boardY) b.getTiles().get(c+1).get(r+1).removeAura(this, "petrifyingAura");
        }
        if (r - 1 >= 0)       b.getTiles().get(c).get(r-1).removeAura(this, "petrifyingAura");
        if (r + 1 < b.boardY) b.getTiles().get(c).get(r+1).removeAura(this, "petrifyingAura");
    }
}
