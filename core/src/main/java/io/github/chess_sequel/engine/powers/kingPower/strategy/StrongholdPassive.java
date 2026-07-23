package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.strategy.Rampart;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Passive strategy king power: Ramparts project a full cross barrier instead of just a
 * row barrier. Enemies cannot cross the column a Rampart occupies (left↔right), in
 * addition to the row barrier already provided by the Rampart's own TollGateAura.
 */
public class StrongholdPassive extends PassiveKingPower {

    public StrongholdPassive(Piece king) {
        super(king, "Stronghold");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (piece.isBlack() == owner.isBlack()) return moves;

        for (Piece p : board.getPieces()) {
            if (!(p instanceof Rampart) || p.isBlack() != owner.isBlack()) continue;

            int rampartCol = p.getCol();
            int pieceCol   = piece.getCol();

            if (pieceCol == rampartCol) continue; // on the barrier column, unrestricted

            Iterator<Move> it = moves.iterator();
            if (pieceCol < rampartCol) {
                while (it.hasNext()) {
                    if (it.next().getNewX() > rampartCol) it.remove();
                }
            } else {
                while (it.hasNext()) {
                    if (it.next().getNewX() < rampartCol) it.remove();
                }
            }
        }

        return moves;
    }

    @Override
    public String getName() { return "Stronghold"; }

    @Override
    public String getIconPath() { return "kingPowers/stronghold.png"; }

    @Override
    public String getDescription() { return "Ramparts project a full cross barrier — enemies cannot cross the column a Rampart occupies, in addition to its row."; }
}
