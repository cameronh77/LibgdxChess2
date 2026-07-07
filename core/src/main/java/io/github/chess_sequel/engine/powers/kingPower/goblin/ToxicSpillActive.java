package io.github.chess_sequel.engine.powers.kingPower.goblin;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.kingMoves.ToxicSpillMove;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Goblin active power: once per game, scatter slime tiles randomly across the board —
 * one tile per surviving friendly piece (including the king).
 */
public class ToxicSpillActive extends ActiveKingPower {

    private final Piece king;

    public ToxicSpillActive(Piece king) {
        this.king = king;
        this.charges = 1;
    }

    @Override
    public String getName() { return "Toxic Spill"; }

    @Override
    public String getIconPath() { return "kingPowers/toxic-spill.png"; }

    @Override
    public String getDescription() { return "Once per game, scatter slime across the board — one tile per surviving friendly piece."; }

    @Override
    public ArrayList<Move> generateMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        if (!isAvailable()) return moves;
        moves.add(new ToxicSpillMove(king, board, this));
        return moves;
    }
}
