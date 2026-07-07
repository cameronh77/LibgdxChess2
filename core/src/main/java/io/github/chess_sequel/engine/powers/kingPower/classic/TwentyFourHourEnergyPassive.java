package io.github.chess_sequel.engine.powers.kingPower.classic;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Pawn;

import java.util.ArrayList;

/**
 * Passive king power: "24 Hour Energy". Friendly pawns that have already moved may still
 * advance two squares forward in a single turn, as if it were their first move.
 * The standard first-move two-square advance already appears in base move generation,
 * so this only adds the move for pawns where {@code isFirstMove} is false.
 */
public class TwentyFourHourEnergyPassive extends PassiveKingPower {

    public TwentyFourHourEnergyPassive(Piece king) {
        super(king, "24 Hour Energy");
    }

    @Override
    public String getIconPath() { return "kingPowers/24hr-energy.png"; }

    @Override
    public String getDescription() { return "Your Pawns can always advance 2 squares forward, not just on their first move."; }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean ignoreCheck) {
        if (!(piece instanceof Pawn)) return moves;
        if (piece.getIsBlack() != owner.getIsBlack()) return moves;
        if (piece.getIsBlack() != board.getWhiteToMove()) return moves;
        if (piece.getIsFirstMove()) return moves; // base move generation already adds it

        int col = piece.getCol();
        int row = piece.getRow();
        int offset = piece.getIsBlack() ? -1 : 1;
        int oneAhead = row + offset;
        int twoAhead = row + 2 * offset;

        if (oneAhead < 0 || oneAhead >= board.boardY) return moves;
        if (twoAhead < 0 || twoAhead >= board.boardY) return moves;
        if (board.getTiles().get(col).get(oneAhead).getPiece() != null) return moves;
        if (board.getTiles().get(col).get(twoAhead).getPiece() != null) return moves;

        Move extra = new Move(piece, col, twoAhead, board);
        if (!ignoreCheck && board.checkEvaluator(extra)) return moves;
        moves.add(extra);
        return moves;
    }
}
