package io.github.chess_sequel.engine.powers.kingPower;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.player.Player;

/**
 * A king power that activates before a match or on victory rather than during one.
 * {@link #apply} is called when the layout board is opened; {@link #onVictory} fires
 * after the player wins a match.
 */
public abstract class PreKingPower implements KingPower {
    /** Called when the AlterLayoutBoard is opened — use to modify the starting team arrangement. */
    public abstract void apply(AlterLayoutBoard board);
    /** Called when the player wins a match — override to grant post-match rewards. */
    public void onVictory(Player player) {}
    /** Short description of what this power does on victory, shown in the win overlay. */
    public String getVictoryDescription() { return null; }
}
