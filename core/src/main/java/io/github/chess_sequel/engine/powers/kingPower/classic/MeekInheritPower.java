package io.github.chess_sequel.engine.powers.kingPower.classic;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.kingMoves.MeekMove;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Pawn;

import java.util.ArrayList;

/**
 * Active king power: "The Meek Shall Inherit". When the king's entire non-king team consists
 * only of Pawns, the king may swap places with any of them, promoting that Pawn to a Queen.
 * Available unconditionally but generates no moves unless the condition is met.
 */
public class MeekInheritPower extends ActiveKingPower {

    private final Piece king;

    public MeekInheritPower(Piece king) {
        this.king = king;
    }

    @Override
    public String getName() { return "The Meek Shall Inherit"; }

    @Override
    public String getIconPath() { return "kingPowers/meek-inherit.png"; }

    @Override
    public String getDescription() { return "When all your non-King pieces are Pawns, the King can swap places with any of them."; }

    @Override
    public boolean isAvailable() { return true; }

    @Override
    public boolean isAvailable(Board board) { return conditionMet(board); }

    private boolean conditionMet(Board board) {
        boolean hasAnyPawn = false;
        for (Piece piece : board.getPieces()) {
            if (piece.getIsBlack() != king.getIsBlack()) continue;
            if (piece == king) continue;
            if (!(piece instanceof Pawn)) return false;
            hasAnyPawn = true;
        }
        return hasAnyPawn;
    }

    @Override
    public ArrayList<Move> generateMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        if (!conditionMet(board)) return moves;
        for (Piece piece : board.getPieces()) {
            if (piece.getIsBlack() != king.getIsBlack()) continue;
            if (piece == king) continue;
            moves.add(new MeekMove(king, board, this, piece));
        }
        return moves;
    }
}
