package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.PersistentTrapMove;
import io.github.chess_sequel.engine.moves.TrapMove;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.strategy.TrapPawn;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;

import java.util.ArrayList;

/**
 * Passive strategy king power: TrapPawns no longer discard their existing trap when placing
 * a new one. Each TrapPawn can accumulate multiple active traps on the board simultaneously.
 */
public class PersistencePassive extends PassiveKingPower {

    public PersistencePassive(Piece king) {
        super(king, "Persistence");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (!(piece instanceof TrapPawn) || piece.isBlack() != owner.isBlack()) return moves;

        for (int i = 0; i < moves.size(); i++) {
            Move m = moves.get(i);
            if (m instanceof TrapMove && !(m instanceof PersistentTrapMove)) {
                moves.set(i, new PersistentTrapMove((TrapPawn) piece, m.getNewX(), m.getNewY(), board));
            }
        }

        return moves;
    }

    @Override
    public String getName() { return "Persistence"; }

    @Override
    public String getIconPath() { return "kingPowers/persistence.png"; }

    @Override
    public String getDescription() { return "TrapPawns keep their old trap when placing a new one, accumulating traps over time."; }
}
