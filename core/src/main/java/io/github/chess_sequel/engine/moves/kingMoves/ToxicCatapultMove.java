package io.github.chess_sequel.engine.moves.kingMoves;

import io.github.chess_sequel.engine.auras.SlimeAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.goblin.StickySlimePassive;

import java.util.ArrayList;
import java.util.List;

/**
 * Places slime on all empty tiles adjacent to the chosen target piece.
 * The target tile is highlighted so the player can click to select it.
 */
public class ToxicCatapultMove extends KingMove {

    private final int targetCol;
    private final int targetRow;
    private final List<SlimeAura> placedSlimes = new ArrayList<>();

    public ToxicCatapultMove(Piece king, int targetCol, int targetRow, Board board, ActiveKingPower power) {
        super(king, targetCol, targetRow, board, power);
        this.targetCol = targetCol;
        this.targetRow = targetRow;
    }

    @Override
    protected void applyEffect() {
        placedSlimes.clear();
        for (int dc = -1; dc <= 1; dc++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (dc == 0 && dr == 0) continue;
                int nc = targetCol + dc, nr = targetRow + dr;
                if (nc < 0 || nc >= board.boardX || nr < 0 || nr >= board.boardY) continue;
                if (board.getTiles().get(nc).get(nr).getPiece() != null) continue;
                int dur = StickySlimePassive.isActive(board, movingPiece.getIsBlack()) ? 2 : 1;
                SlimeAura aura = new SlimeAura(nc, nr, movingPiece.getIsBlack(), dur);
                placedSlimes.add(aura);
                board.addAura(aura);
            }
        }
    }

    @Override
    protected void revertEffect() {
        for (SlimeAura aura : placedSlimes) {
            board.removeAura(aura);
        }
        placedSlimes.clear();
    }
}
