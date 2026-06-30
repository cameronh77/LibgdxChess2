package io.github.chess_sequel.engine.pieces.factories;

import io.github.chess_sequel.engine.interactables.ActivePowerEffect;
import io.github.chess_sequel.engine.interactables.PassivePowerEffect;
import io.github.chess_sequel.engine.interactables.PreGamePowerEffect;
import io.github.chess_sequel.engine.interactables.ShopEffect;
import io.github.chess_sequel.engine.powers.kingPower.BouncingBishopsPassive;
import io.github.chess_sequel.engine.powers.kingPower.MeekInheritPower;
import io.github.chess_sequel.engine.powers.kingPower.WinBonus;

public class KingPowerFactory {

    public static ShopEffect createEffect(String name) {
        switch (name) {
            case "power-meek-inherit":
                return new ActivePowerEffect(
                    MeekInheritPower::new,
                    "The Meek Shall Inherit",
                    "kingPowers/meek-inherit.png",
                    "If all your pieces are pawns, swap one with your king."
                );
            case "power-bouncing-bishops":
                return new PassivePowerEffect(
                    BouncingBishopsPassive::new,
                    "Bouncing Bishops",
                    "kingPowers/bouncing-bishops.png",
                    "Bishops reflect off board edges to continue their diagonal."
                );
            case "power-win-bonus":
                return new PreGamePowerEffect(
                    k -> new WinBonus(),
                    "Victory Bonus",
                    "kingPowers/win-bonus.png",
                    "+5 Gold on victory."
                );
            default:
                return null;
        }
    }
}
