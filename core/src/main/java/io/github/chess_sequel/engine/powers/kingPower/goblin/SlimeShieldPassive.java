package io.github.chess_sequel.engine.powers.kingPower.goblin;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.auras.SlimeAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.goblin.StickySlimePassive;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Passive goblin king power: when the king moves, slime appears on each empty cardinal
 * (N/S/E/W) tile adjacent to its landing square. Max 4 slimes per move — one short of
 * the GoblinQueen tier in Create Life, keeping single-move summons capped at TollGate.
 *
 * A history stack tracks placed slimes per move so minimax undo works correctly.
 */
public class SlimeShieldPassive extends PassiveKingPower {

    private static final int[][] CARDINALS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private final Deque<List<SlimeAura>> slimeHistory = new ArrayDeque<>();

    public SlimeShieldPassive(Piece king) {
        super(king, "Slime Shield");
    }

    @Override
    public String getIconPath() { return "kingPowers/slime-shield.png"; }

    @Override
    public String getDescription() { return "When your King moves, slime appears on adjacent cardinal tiles."; }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        return moves;
    }

    @Override
    public void onLand(Piece piece, int landedX, int landedY, Board board) {
        if (piece != owner) return;
        List<SlimeAura> placed = new ArrayList<>();
        for (int[] dir : CARDINALS) {
            int nc = landedX + dir[0], nr = landedY + dir[1];
            if (nc < 0 || nc >= board.boardX || nr < 0 || nr >= board.boardY) continue;
            if (board.getTiles().get(nc).get(nr).getPiece() != null) continue;
            int dur = StickySlimePassive.isActive(board, owner.getIsBlack()) ? 2 : 1;
            SlimeAura aura = new SlimeAura(nc, nr, owner.getIsBlack(), dur);
            placed.add(aura);
            board.addAura(aura);
        }
        slimeHistory.push(placed);
    }

    @Override
    public void onUndoLand(Piece piece, int landedX, int landedY, Board board) {
        if (piece != owner) return;
        if (slimeHistory.isEmpty()) return;
        for (SlimeAura aura : slimeHistory.pop()) {
            board.removeAura(aura);
        }
    }
}
