package io.github.chess_sequel.engine.powers.kingPower;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.player.Player;

public abstract class PreKingPower implements KingPower {
    public abstract void apply(AlterLayoutBoard board);
    public void onVictory(Player player) {}
    public String getVictoryDescription() { return null; }
}
