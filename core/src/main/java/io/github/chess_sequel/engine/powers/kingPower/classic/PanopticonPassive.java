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
 * Passive king power: "Panopticon". Friendly pawns may move one square in any
 * orthogonal direction (non-capturing), not just forward.
 */
public class PanopticonPassive extends PassiveKingPower {

    public PanopticonPassive(Piece king) {
        super(king, "Panopticon");
    }

    @Override
    public String getIconPath() { return "kingPowers/panopticon.png"; }

    @Override
    public String getDescription() { return "Your Pawns can move one square in any orthogonal direction (non-capturing)."; }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean ignoreCheck) {
        if (!(piece instanceof Pawn)) return moves;
        if (piece.getIsBlack() != owner.getIsBlack()) return moves;
        if (piece.getIsBlack() != board.getWhiteToMove()) return moves;

        int col = piece.getCol();
        int row = piece.getRow();

        ArrayList<Move> extra = new ArrayList<>();
        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
        for (int[] dir : dirs) {
            int c = col + dir[0], r = row + dir[1];
            if (c < 0 || c >= board.boardX || r < 0 || r >= board.boardY) continue;
            if (board.getTiles().get(c).get(r).getPiece() != null) continue;
            boolean dup = false;
            for (Move m : moves) {
                if (m.getNewX() == c && m.getNewY() == r) { dup = true; break; }
            }
            if (!dup) extra.add(new Move(piece, c, r, board));
        }

        if (!ignoreCheck) {
            ArrayList<Move> safe = new ArrayList<>();
            for (Move m : extra) {
                if (!board.checkEvaluator(m)) safe.add(m);
            }
            extra = safe;
        }

        moves.addAll(extra);
        return moves;
    }
}
