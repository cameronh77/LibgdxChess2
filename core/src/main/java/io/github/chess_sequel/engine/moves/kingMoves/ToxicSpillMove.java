package io.github.chess_sequel.engine.moves.kingMoves;

import io.github.chess_sequel.engine.auras.SlimeAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.goblin.StickySlimePassive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Scatters slime tiles randomly across the board — one per surviving friendly piece.
 * Slimes are tracked on the move instance for clean undo support.
 */
public class ToxicSpillMove extends KingMove {

    private final List<SlimeAura> placedSlimes = new ArrayList<>();

    public ToxicSpillMove(Piece king, Board board, ActiveKingPower power) {
        super(king, board, power);
    }

    @Override
    protected void applyEffect() {
        placedSlimes.clear();

        List<int[]> openTiles = new ArrayList<>();
        for (int c = 0; c < board.boardX; c++) {
            for (int r = 0; r < board.boardY; r++) {
                if (board.getTiles().get(c).get(r).getPiece() == null) {
                    openTiles.add(new int[]{c, r});
                }
            }
        }

        int count = 0;
        for (Piece p : board.getPieces()) {
            if (p.getIsBlack() == movingPiece.getIsBlack()) count++;
        }

        Collections.shuffle(openTiles);
        int toPlace = Math.min(count, openTiles.size());
        for (int i = 0; i < toPlace; i++) {
            int dur = StickySlimePassive.isActive(board, movingPiece.getIsBlack()) ? 2 : 1;
            SlimeAura aura = new SlimeAura(openTiles.get(i)[0], openTiles.get(i)[1], movingPiece.getIsBlack(), dur);
            placedSlimes.add(aura);
            board.addAura(aura);
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
