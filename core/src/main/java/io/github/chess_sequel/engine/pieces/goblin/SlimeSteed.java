package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.auras.SlimeAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Horse;
import io.github.chess_sequel.engine.powers.kingPower.goblin.StickySlimePassive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Goblin faction knight. Moves like a Horse but when it is captured it places
 * {@link io.github.chess_sequel.engine.auras.SlimeAura} on 2 randomly chosen adjacent tiles,
 * slowing enemy pieces that land on them.
 */
public class SlimeSteed extends Horse {

    private Board activeBoard;
    private final List<SlimeAura> placedSlimes = new ArrayList<>();

    public SlimeSteed(int x, int y, boolean isBlack){
        super(x, y, isBlack, "slime-steed", ChessClass.GOBLIN);
    }

    @Override
    public String getDescription() { return "Jumps like a Horse. On capture, leaves slime on 2 nearby tiles — pieces entering them are slowed."; }

    @Override
    public void onStart(Board board) {
        this.activeBoard = board;
    }

    @Override
    public void onCapture(Piece piece) {
        if (activeBoard == null) return;

        List<int[]> candidates = new ArrayList<>();
        for (int dc = -1; dc <= 1; dc++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (dc == 0 && dr == 0) continue;
                int nc = col + dc, nr = row + dr;
                if (nc >= 0 && nc < activeBoard.boardX && nr >= 0 && nr < activeBoard.boardY) {
                    candidates.add(new int[]{nc, nr});
                }
            }
        }

        Collections.shuffle(candidates);
        placedSlimes.clear();
        int count = Math.min(2, candidates.size());
        for (int i = 0; i < count; i++) {
            int dur = StickySlimePassive.isActive(activeBoard, isBlack) ? 2 : 1;
            SlimeAura aura = new SlimeAura(candidates.get(i)[0], candidates.get(i)[1], isBlack, dur);
            placedSlimes.add(aura);
            activeBoard.addAura(aura);
        }
    }

    @Override
    public void undoOnCapture(Piece piece) {
        for (SlimeAura aura : placedSlimes) {
            activeBoard.removeAura(aura);
        }
        placedSlimes.clear();
    }
}
