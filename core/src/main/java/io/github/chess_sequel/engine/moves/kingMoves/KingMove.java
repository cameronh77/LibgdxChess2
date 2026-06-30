package io.github.chess_sequel.engine.moves.kingMoves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;

public class KingMove extends Move {

    protected final ActiveKingPower power;

    public KingMove(Piece king, Board board, ActiveKingPower power) {
        super(king, king.getCol(), king.getRow(), board);
        this.capturedPiece = null; // king occupies this tile; super() would wrongly set it to the king itself
        this.power = power;
    }

    protected KingMove(Piece king, int targetCol, int targetRow, Board board, ActiveKingPower power) {
        super(king, targetCol, targetRow, board);
        this.capturedPiece = null;
        this.power = power;
    }

    protected void applyEffect() {}
    protected void revertEffect() {}

    @Override
    public void execute() {
        power.spendCharge();
        applyEffect();
        if (board instanceof MatchBoard) board.setWhiteToMove(!board.getWhiteToMove());
        board.tick();
    }

    @Override
    public void undo() {
        board.untick();
        if (board instanceof MatchBoard) board.setWhiteToMove(!board.getWhiteToMove());
        revertEffect();
        power.refundCharge();
    }
}
