package io.github.chess_sequel.engine.powers.kingPower.goblin;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.auras.SlimeAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.kingMoves.CreateLifeMove;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Goblin active power: unlimited charges. Consumes all friendly slime tiles to summon
 * a goblin piece on a chosen cardinally adjacent empty tile. Tier scales with slime count:
 * 1 → Goblin, 2 → SlimeSteed, 3 → GoblinDrill, 4 → TollGate, 5+ → GoblinQueen.
 * Unavailable if there are no unconsumed friendly slimes.
 */
public class CreateLifeActive extends ActiveKingPower {

    private static final int[][] CARDINALS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private final Piece king;

    public CreateLifeActive(Piece king) {
        this.king = king;
        this.charges = -1; // unlimited
    }

    @Override
    public String getName() { return "Create Life"; }

    @Override
    public String getIconPath() { return "kingPowers/create-life.png"; }

    @Override
    public String getDescription() { return "Consume all slime to summon a goblin piece — the more slime, the stronger the piece."; }

    @Override
    public boolean isAvailable() { return true; }

    @Override
    public boolean isAvailable(Board board) {
        boolean isBlack = king.getIsBlack();
        for (Aura a : board.getBoardAuras()) {
            if (a instanceof SlimeAura) {
                SlimeAura s = (SlimeAura) a;
                if (s.isBlackOwned() == isBlack && !s.isConsumed()) return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<Move> generateMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        if (!isAvailable(board)) return moves;
        int col = king.getCol(), row = king.getRow();
        for (int[] dir : CARDINALS) {
            int nc = col + dir[0], nr = row + dir[1];
            if (nc < 0 || nc >= board.boardX || nr < 0 || nr >= board.boardY) continue;
            if (board.getTiles().get(nc).get(nr).getPiece() != null) continue;
            moves.add(new CreateLifeMove(king, nc, nr, board, this));
        }
        return moves;
    }
}
