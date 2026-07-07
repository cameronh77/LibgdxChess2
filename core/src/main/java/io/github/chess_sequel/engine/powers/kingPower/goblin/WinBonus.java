package io.github.chess_sequel.engine.powers.kingPower.goblin;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.player.Player;

/** Pre-game king power: "Victory Bonus". Awards +5 gold to the player after each match won. */
public class WinBonus extends PreKingPower {

    @Override
    public String getName() { return "Victory Bonus"; }

    @Override
    public String getIconPath() { return "kingPowers/win-bonus.png"; }

    @Override
    public String getDescription() { return "Earn +5 Gold whenever you win a match."; }

    @Override
    public void apply(AlterLayoutBoard board) {}

    @Override
    public void onVictory(Player player) {
        player.incrementCurrency(5);
    }

    @Override
    public String getVictoryDescription() { return "+5 Gold"; }
}
