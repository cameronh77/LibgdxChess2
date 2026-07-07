package io.github.chess_sequel.engine.powers.kingPower.goblin;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.TollGate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Passive goblin king power: friendly pieces sharing a row with a TollGate cannot be captured.
 * The TollGate itself remains vulnerable — destroying it removes all protection on that row.
 */
public class FortifyPassive extends PassiveKingPower {

    public FortifyPassive(Piece king) {
        super(king, "Fortify");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (piece.isBlack() == owner.isBlack()) return moves;

        Set<Integer> tollRows = new HashSet<>();
        for (Piece p : board.getPieces()) {
            if (p.isBlack() == owner.isBlack() && p instanceof TollGate) {
                tollRows.add(p.getRow());
            }
        }
        if (tollRows.isEmpty()) return moves;

        moves.removeIf(move -> {
            if (!tollRows.contains(move.getNewY())) return false;
            Piece target = board.getTiles().get(move.getNewX()).get(move.getNewY()).getPiece();
            if (target == null) return false;
            if (target.isBlack() != owner.isBlack()) return false;
            if (target instanceof TollGate) return false; // TollGate is not protected
            return true;
        });

        return moves;
    }

    @Override
    public String getIconPath() { return "kingPowers/fortify.png"; }

    @Override
    public String getDescription() { return "Friendly pieces on a TollGate's row cannot be captured. Only the TollGate itself remains vulnerable."; }
}
