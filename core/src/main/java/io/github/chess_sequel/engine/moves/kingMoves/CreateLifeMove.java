package io.github.chess_sequel.engine.moves.kingMoves;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.auras.SlimeAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.Goblin;
import io.github.chess_sequel.engine.pieces.goblin.GoblinDrill;
import io.github.chess_sequel.engine.pieces.goblin.GoblinQueen;
import io.github.chess_sequel.engine.pieces.goblin.SlimeSteed;
import io.github.chess_sequel.engine.pieces.goblin.TollGate;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;

import java.util.ArrayList;
import java.util.List;

/**
 * Consumes all friendly slime tiles and summons a goblin piece at the chosen cardinal tile.
 * Tier is determined by the number of unconsumed friendly slimes at the time of execution:
 * 1 → Goblin, 2 → SlimeSteed, 3 → GoblinDrill, 4 → TollGate, 5+ → GoblinQueen.
 */
public class CreateLifeMove extends KingMove {

    private final List<SlimeAura> consumedSlimes = new ArrayList<>();
    private Piece placedPiece;

    public CreateLifeMove(Piece king, int targetCol, int targetRow, Board board, ActiveKingPower power) {
        super(king, targetCol, targetRow, board, power);
    }

    @Override
    protected void applyEffect() {
        boolean isBlack = movingPiece.getIsBlack();

        List<SlimeAura> friendly = getFriendlySlimes(isBlack);
        int tier = 0;
        for (SlimeAura s : friendly) {
            if (!s.isConsumed()) tier++;
        }

        consumedSlimes.clear();
        consumedSlimes.addAll(friendly);
        for (SlimeAura s : friendly) board.removeAura(s);

        placedPiece = pieceForTier(tier, newX, newY, isBlack);
        if (placedPiece != null) {
            board.getPieces().add(placedPiece);
            board.getTiles().get(newX).get(newY).setPiece(placedPiece);
            placedPiece.onStart(board);
        }
    }

    @Override
    protected void revertEffect() {
        if (placedPiece != null) {
            board.getPieces().remove(placedPiece);
            board.getTiles().get(newX).get(newY).setPiece(null);
            placedPiece = null;
        }
        for (SlimeAura s : consumedSlimes) board.addAura(s);
        consumedSlimes.clear();
    }

    private List<SlimeAura> getFriendlySlimes(boolean isBlack) {
        List<SlimeAura> result = new ArrayList<>();
        for (Aura a : board.getBoardAuras()) {
            if (a instanceof SlimeAura && ((SlimeAura) a).isBlackOwned() == isBlack) {
                result.add((SlimeAura) a);
            }
        }
        return result;
    }

    private Piece pieceForTier(int tier, int col, int row, boolean isBlack) {
        switch (tier) {
            case 0:  return null;
            case 1:  return new Goblin(col, row, isBlack);
            case 2:  return new SlimeSteed(col, row, isBlack);
            case 3:  return new GoblinDrill(col, row, isBlack);
            case 4:  return new TollGate(col, row, isBlack);
            default: return new GoblinQueen(col, row, isBlack);
        }
    }
}
