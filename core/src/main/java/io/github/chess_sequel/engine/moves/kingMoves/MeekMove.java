package io.github.chess_sequel.engine.moves.kingMoves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Queen;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;

public class MeekMove extends KingMove {

    private final Piece targetPawn;
    private Piece promotedQueen;

    public MeekMove(Piece king, Board board, ActiveKingPower power, Piece targetPawn) {
        super(king, targetPawn.getCol(), targetPawn.getRow(), board, power);
        this.targetPawn = targetPawn;
    }

    @Override
    protected void applyEffect() {
        int col = targetPawn.getCol();
        int row = targetPawn.getRow();
        board.getTiles().get(col).get(row).setPiece(null);
        board.getPieces().remove(targetPawn);
        promotedQueen = new Queen(col, row, targetPawn.getIsBlack());
        promotedQueen.setCol(col);
        promotedQueen.setRow(row);
        board.getPieces().add(promotedQueen);
        board.getTiles().get(col).get(row).setPiece(promotedQueen);
    }

    @Override
    protected void revertEffect() {
        int col = targetPawn.getCol();
        int row = targetPawn.getRow();
        board.getTiles().get(col).get(row).setPiece(null);
        board.getPieces().remove(promotedQueen);
        promotedQueen = null;
        board.getPieces().add(targetPawn);
        board.getTiles().get(col).get(row).setPiece(targetPawn);
    }
}
