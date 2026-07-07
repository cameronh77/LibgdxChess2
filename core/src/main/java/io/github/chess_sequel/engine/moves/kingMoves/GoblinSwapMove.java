package io.github.chess_sequel.engine.moves.kingMoves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;

/**
 * Swaps the king's position with a target allied piece. Neither piece is captured;
 * the king teleports to the target's square and the target moves to the king's old square.
 */
public class GoblinSwapMove extends KingMove {

    private final Piece king;
    private final Piece target;

    public GoblinSwapMove(Piece king, Piece target, Board board, ActiveKingPower power) {
        super(king, target.getCol(), target.getRow(), board, power);
        this.king = king;
        this.target = target;
    }

    @Override
    protected void applyEffect() {
        board.getTiles().get(oldX).get(oldY).setPiece(target);
        board.getTiles().get(newX).get(newY).setPiece(king);
        king.setCol(newX);
        king.setRow(newY);
        target.setCol(oldX);
        target.setRow(oldY);
    }

    @Override
    protected void revertEffect() {
        board.getTiles().get(oldX).get(oldY).setPiece(king);
        board.getTiles().get(newX).get(newY).setPiece(target);
        king.setCol(oldX);
        king.setRow(oldY);
        target.setCol(newX);
        target.setRow(newY);
    }
}
