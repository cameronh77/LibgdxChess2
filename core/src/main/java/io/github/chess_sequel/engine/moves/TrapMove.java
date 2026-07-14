package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.TrapAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.strategy.TrapPawn;

/**
 * Places a {@link TrapAura} on a diagonal tile without moving the pawn itself.
 * If a trap was already set, it is removed first.
 * newX/newY stay at the pawn's position so no movement occurs; trap coords are
 * stored separately and returned via getNewX/getNewY for UI tile highlighting.
 */
public class TrapMove extends Move {

    private final TrapAura newTrap;
    private final TrapAura previousTrap;
    private final int trapCol;
    private final int trapRow;

    public TrapMove(TrapPawn pawn, int trapCol, int trapRow, Board board) {
        super(pawn, pawn.getCol(), pawn.getRow(), board);
        this.capturedPiece = null;
        this.trapCol = trapCol;
        this.trapRow = trapRow;
        this.newTrap = new TrapAura(pawn, trapCol, trapRow);
        this.previousTrap = pawn.getCurrentTrap();
    }

    @Override public int getNewX() { return trapCol; }
    @Override public int getNewY() { return trapRow; }

    @Override
    public void execute() {
        previousCondition = board.getTurnCondition();

        if (previousTrap != null) {
            board.getBoardAuras().remove(previousTrap);
        }

        board.getBoardAuras().add(newTrap);
        ((TrapPawn) movingPiece).setCurrentTrap(newTrap);

        board.setEnPassantTile(null);

        if (board instanceof MatchBoard && endsTurn()) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        board.tick();
    }

    @Override
    public void undo() {
        board.untick();

        board.getBoardAuras().remove(newTrap);

        if (previousTrap != null) {
            board.getBoardAuras().add(previousTrap);
        }

        ((TrapPawn) movingPiece).setCurrentTrap(previousTrap);

        board.setEnPassantTile(enPassantTile);

        if (board instanceof MatchBoard && endsTurn()) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        board.setTurnCondition(previousCondition);
    }

    @Override
    public boolean endsTurn() { return true; }
}
