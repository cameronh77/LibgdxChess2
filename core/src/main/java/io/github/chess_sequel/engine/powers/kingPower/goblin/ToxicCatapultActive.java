package io.github.chess_sequel.engine.powers.kingPower.goblin;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.kingMoves.ToxicCatapultMove;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Goblin active power: once per game, surround any piece on the board with slime tiles.
 * Generates one move per piece — the player clicks the target piece to confirm.
 */
public class ToxicCatapultActive extends ActiveKingPower {

    private final Piece king;

    public ToxicCatapultActive(Piece king) {
        this.king = king;
        this.charges = 1;
    }

    @Override
    public String getName() { return "Toxic Catapult"; }

    @Override
    public String getIconPath() { return "kingPowers/toxic-catapult.png"; }

    @Override
    public String getDescription() { return "Once per game, surround any piece with slime."; }

    @Override
    public ArrayList<Move> generateMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        if (!isAvailable()) return moves;
        for (Piece p : board.getPieces()) {
            moves.add(new ToxicCatapultMove(king, p.getCol(), p.getRow(), board, this));
        }
        return moves;
    }
}
