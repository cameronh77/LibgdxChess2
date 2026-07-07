package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.goblin.Goblin;

/**
 * Move used by {@link io.github.chess_sequel.engine.pieces.goblin.GoblinQueen}.
 * Executes a standard queen move then spawns a friendly {@link Goblin} pawn on the departure square.
 * The spawned Goblin is tracked for full undo support.
 */
public class BroodmotherMove extends Move {

    private Goblin spawnedGoblin;

    public BroodmotherMove(Piece piece, int newX, int newY, Board board) {
        super(piece, newX, newY, board);
    }

    @Override
    public void execute() {
        board.getTiles().get(oldX).get(oldY).setPiece(null);

        board.getPieces().remove(capturedPiece);
        board.getTiles().get(newX).get(newY).setPiece(movingPiece);
        movingPiece.setCol(newX);
        movingPiece.setRow(newY);
        movingPiece.setIsFirstMove(false);

        if (movingPiece.getName() == "pawn" && Math.abs(oldY - newY) == 2) {
            int[] enPassantTile = {newX, (oldY + newY) / 2};
            board.setEnPassantTile(enPassantTile);
        } else {
            board.setEnPassantTile(null);
        }

        spawnedGoblin = new Goblin(oldX, oldY, movingPiece.getIsBlack());
        board.getTiles().get(oldX).get(oldY).setPiece(spawnedGoblin);
        board.getPieces().add(spawnedGoblin);

        if (board instanceof MatchBoard && endsTurn()) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        if (capturedPiece != null) {
            capturedPiece.onCapture(movingPiece);
        }

        board.tick();
    }

    @Override
    public void undo() {
        board.getTiles().get(newX).get(newY).setPiece(capturedPiece);

        if (spawnedGoblin != null) {
            board.getPieces().remove(spawnedGoblin);
            board.getTiles().get(oldX).get(oldY).setPiece(null);
            spawnedGoblin = null;
        }

        if (capturedPiece != null) {
            board.getPieces().add(capturedPiece);
        }

        board.getTiles().get(oldX).get(oldY).setPiece(movingPiece);
        movingPiece.setCol(oldX);
        movingPiece.setRow(oldY);
        movingPiece.setIsFirstMove(isFirstMove);
        board.setEnPassantTile(enPassantTile);

        if (board instanceof MatchBoard && endsTurn()) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        board.untick();

        if (capturedPiece != null) {
            capturedPiece.undoOnCapture(movingPiece);
        }
    }
}
