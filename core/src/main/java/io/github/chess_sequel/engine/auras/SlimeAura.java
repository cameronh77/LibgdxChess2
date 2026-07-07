package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.pieceAltering.Stuck;

import java.util.ArrayList;

/**
 * Board-level aura dropped by {@link io.github.chess_sequel.engine.pieces.goblin.SlimeSteed} on capture.
 * Blocks enemy pieces from moving through the slimed tile while unconsumed, then applies a
 * {@link io.github.chess_sequel.engine.powers.pieceAltering.Stuck} debuff to the first enemy that lands on it.
 * Single-use — consumed after the first landing.
 */
public class SlimeAura extends Aura {

    private final boolean isBlackOwned;
    private boolean consumed = false;
    private final Stuck stuck;

    public SlimeAura(int col, int row, boolean isBlackOwned) {
        this(col, row, isBlackOwned, 1);
    }

    public SlimeAura(int col, int row, boolean isBlackOwned, int stuckDuration) {
        super(null, "slimeAura");
        this.auraCol = col;
        this.auraRow = row;
        this.isBlackOwned = isBlackOwned;
        this.imagePath = "tileModifiers/slime.png";
        this.stuck = new Stuck(stuckDuration);
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (consumed) return moves;
        if (piece.isBlack() == isBlackOwned) return moves;

        int pc = piece.getCol(), pr = piece.getRow();
        moves.removeIf(move -> isBlockedBy(pc, pr, move.getNewX(), move.getNewY()));
        return moves;
    }

    @Override
    public void onLand(Piece piece, int landedX, int landedY, Board board) {
        if (consumed) return;
        if (piece.isBlack() == isBlackOwned) return;
        if (landedX == auraCol && landedY == auraRow) {
            piece.getAlterMovePowers().add(stuck);
            consumed = true;
        }
    }

    @Override
    public void onUndoLand(Piece piece, int landedX, int landedY, Board board) {
        if (!consumed) return;
        if (piece.isBlack() == isBlackOwned) return;
        if (landedX == auraCol && landedY == auraRow) {
            piece.getAlterMovePowers().remove(stuck);
            consumed = false;
        }
    }

    public boolean isBlackOwned() { return isBlackOwned; }
    public boolean isConsumed()   { return consumed; }

    // Returns true if (auraCol, auraRow) lies strictly between (pc,pr) and (tc,tr) on a straight line
    private boolean isBlockedBy(int pc, int pr, int tc, int tr) {
        int dx = tc - pc, dy = tr - pr;
        int sx = auraCol - pc, sy = auraRow - pr;
        if (dx * sy != dy * sx) return false; // not collinear
        if (dx != 0) {
            return Integer.signum(sx) == Integer.signum(dx) && Math.abs(sx) < Math.abs(dx);
        } else if (dy != 0) {
            return Integer.signum(sy) == Integer.signum(dy) && Math.abs(sy) < Math.abs(dy);
        }
        return false;
    }
}
