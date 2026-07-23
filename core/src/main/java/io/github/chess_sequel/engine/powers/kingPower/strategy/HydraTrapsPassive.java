package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.auras.TrapAura;
import io.github.chess_sequel.engine.auras.TrapTriggerListener;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.strategy.TrapPawn;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Passive strategy king power: when any friendly trap is triggered, a new trap is placed
 * at a random empty tile on the board, still owned by the pawn whose trap fired.
 * Fully undo-safe: placed traps are tracked per trigger location and removed on undo.
 */
public class HydraTrapsPassive extends PassiveKingPower implements TrapTriggerListener {

    // Keyed by "triggerCol,triggerRow" → stack of traps placed (stack for minimax undo depth)
    private final Map<String, Deque<TrapAura>> placed = new HashMap<>();

    public HydraTrapsPassive(Piece king) {
        super(king, "HydraTraps");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        return moves;
    }

    @Override
    public void onTrapTriggered(Piece trapOwner, int triggerCol, int triggerRow, Board board) {
        if (!(trapOwner instanceof TrapPawn)) return;
        TrapAura hydra = placeRandom((TrapPawn) trapOwner, board);
        if (hydra == null) return;
        String key = triggerCol + "," + triggerRow;
        placed.computeIfAbsent(key, k -> new ArrayDeque<>()).push(hydra);
    }

    @Override
    public void onUndoLand(Piece piece, int landedX, int landedY, Board board) {
        String key = landedX + "," + landedY;
        Deque<TrapAura> stack = placed.get(key);
        if (stack == null || stack.isEmpty()) return;
        TrapAura trap = stack.pop();
        board.getBoardAuras().remove(trap);
        if (stack.isEmpty()) placed.remove(key);
    }

    private TrapAura placeRandom(TrapPawn pawn, Board board) {
        ArrayList<int[]> candidates = new ArrayList<>();
        for (int c = 0; c < board.boardX; c++) {
            for (int r = 0; r < board.boardY; r++) {
                if (board.getTiles().get(c).get(r).getPiece() != null) continue;
                if (hasTrapAt(c, r, board)) continue;
                candidates.add(new int[]{c, r});
            }
        }
        if (candidates.isEmpty()) return null;
        int[] pos = candidates.get((int) (Math.random() * candidates.size()));
        TrapAura trap = new TrapAura(pawn, pos[0], pos[1]);
        board.addAura(trap);
        return trap;
    }

    private boolean hasTrapAt(int col, int row, Board board) {
        for (Aura aura : board.getBoardAuras()) {
            if (aura instanceof TrapAura && aura.getAuraCol() == col && aura.getAuraRow() == row) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() { return "Hydra Traps"; }

    @Override
    public String getIconPath() { return "kingPowers/hydra-traps.png"; }

    @Override
    public String getDescription() { return "When a friendly trap is triggered, a new trap is placed on a random empty tile, owned by the same pawn."; }
}
