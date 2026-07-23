package io.github.chess_sequel.engine.moves.kingMoves;

import io.github.chess_sequel.engine.auras.ZugzwangAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;

public class ZugzwangMove extends KingMove {

    private final Piece targetPiece;
    private ZugzwangAura aura;

    public ZugzwangMove(Piece king, Piece targetPiece, Board board, ActiveKingPower power) {
        super(king, targetPiece.getCol(), targetPiece.getRow(), board, power);
        this.targetPiece = targetPiece;
    }

    @Override
    protected void applyEffect() {
        aura = new ZugzwangAura(movingPiece, targetPiece);
        board.addAura(aura);
    }

    @Override
    protected void revertEffect() {
        board.removeAura(aura);
        aura = null;
    }
}
