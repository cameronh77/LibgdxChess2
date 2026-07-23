package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.goblin.BloodFrenzyPassive;

import java.util.ArrayList;

/**
 * The Trebuchet fires at a piece in its orthogonal line of sight without moving.
 * {@code newX/newY} hold the target's coordinates (used for undo/capturedPiece resolution);
 * the Trebuchet's own position never changes during execute or undo.
 */
public class TrebuchetFireMove extends Move {

    public TrebuchetFireMove(Piece trebuchet, Piece target, int targetX, int targetY, Board board) {
        super(trebuchet, targetX, targetY, board);
        // capturedPiece is set to target by Move's constructor since target occupies (targetX, targetY)
    }

    @Override
    public void execute() {
        previousCondition = board.getTurnCondition();

        // Remove the target — trebuchet stays at (oldX, oldY)
        board.getTiles().get(newX).get(newY).setPiece(null);
        board.getPieces().remove(capturedPiece);

        board.setEnPassantTile(null);

        boolean frenzyCapture = capturedPiece != null && BloodFrenzyPassive.isActive(board, movingPiece.isBlack());
        if (board instanceof MatchBoard && endsTurn() && !frenzyCapture) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        capturedPiece.onCapture(movingPiece);

        for (Aura aura : new ArrayList<>(board.getBoardAuras())) {
            aura.onLand(movingPiece, oldX, oldY, board);
        }

        if (frenzyCapture) {
            TurnCondition c = board.getTurnCondition();
            if (c == null || c.frenzySide != movingPiece.isBlack()) {
                c = new TurnCondition(movingPiece.isBlack());
            }
            board.setTurnCondition(c.withActor(movingPiece));
        } else if (board.getTurnCondition() != null && board.getTurnCondition().frenzySide == movingPiece.isBlack()) {
            board.setTurnCondition(null);
        }

        board.tick();

        for (Piece piece : new ArrayList<>(board.getPieces())) {
            piece.postMove(this, board);
        }
    }

    @Override
    public void undo() {
        for (Aura aura : new ArrayList<>(board.getBoardAuras())) {
            aura.onUndoLand(movingPiece, oldX, oldY, board);
        }

        // Restore target to its tile — trebuchet was never moved
        board.getTiles().get(newX).get(newY).setPiece(capturedPiece);
        if (capturedPiece != null) board.getPieces().add(capturedPiece);

        board.setEnPassantTile(enPassantTile);

        boolean frenzyCapture = capturedPiece != null && BloodFrenzyPassive.isActive(board, movingPiece.isBlack());
        if (board instanceof MatchBoard && endsTurn() && !frenzyCapture) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        board.setTurnCondition(previousCondition);

        for (Piece piece : new ArrayList<>(board.getPieces())) {
            piece.undoPostMove(this, board);
        }

        board.untick();

        if (capturedPiece != null) capturedPiece.undoOnCapture(movingPiece);
    }
}
