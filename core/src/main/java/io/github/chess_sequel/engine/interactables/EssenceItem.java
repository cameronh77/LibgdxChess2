package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.pieces.classic.King;
import io.github.chess_sequel.engine.powers.pieceAltering.AlterMovePower;
import io.github.chess_sequel.engine.powers.pieceAltering.MapEssenceAlterMove;

/**
 * 1-use map item: grants the player's king an alternate movement pattern for exactly one
 * map step. Valid pieceType values: "queen", "rook", "bishop", "horse".
 */
public class EssenceItem extends ConsumableItem {

    private final String pieceType;

    public EssenceItem(String pieceType) {
        this.pieceType = pieceType;
    }

    public String getPieceType() { return pieceType; }

    @Override public String getName() { return "Essence of " + capitalize(pieceType); }
    @Override public String getIconPath() { return "items/essence-" + pieceType + ".png"; }
    @Override public String getDescription() {
        return "Your king moves like a " + pieceType + " for one step on the map.";
    }

    @Override
    public void onActivate(GameRun game) {
        King king = game.getPlayer().getKing();
        if (king != null) king.getAlterMovePowers().add(new MapEssenceAlterMove(king, pieceType));
    }

    @Override
    public boolean isActive(GameRun game) {
        King king = game.getPlayer().getKing();
        if (king == null) return false;
        java.util.Iterator<AlterMovePower> it = king.getAlterMovePowers().iterator();
        while (it.hasNext()) {
            AlterMovePower p = it.next();
            if (p instanceof MapEssenceAlterMove && ((MapEssenceAlterMove) p).getPieceType().equals(pieceType)) {
                if (p.getDuration() > 0) return true;
                // Expired after move was taken — clean up the power and consume the item
                it.remove();
                game.getPlayer().getConsumables().remove(this);
                return false;
            }
        }
        return false;
    }

    @Override
    public void onDeactivate(GameRun game) {
        King king = game.getPlayer().getKing();
        if (king == null) return;
        king.getAlterMovePowers().removeIf(
            p -> p instanceof MapEssenceAlterMove && ((MapEssenceAlterMove) p).getPieceType().equals(pieceType)
        );
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
