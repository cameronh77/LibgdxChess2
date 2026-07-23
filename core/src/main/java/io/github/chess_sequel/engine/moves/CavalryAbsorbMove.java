package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.conflict.Cavalry;
import io.github.chess_sequel.engine.pieces.war.conflict.CombinedPiece;
import io.github.chess_sequel.engine.powers.kingPower.goblin.BloodFrenzyPassive;

import java.util.ArrayList;

/**
 * A friendly piece moves onto the Cavalry's tile and the two merge into a CombinedPiece
 * that inherits both movement sets. The absorbed piece and the Cavalry are both removed
 * from the board; the CombinedPiece replaces them at the Cavalry's position.
 */
public class CavalryAbsorbMove extends Move {

    private final Cavalry cavalry;
    private final CombinedPiece combined;

    public CavalryAbsorbMove(Piece absorber, Cavalry cavalry, Board board) {
        super(absorber, cavalry.getCol(), cavalry.getRow(), board);
        // capturedPiece is set to cavalry by Move's constructor (cavalry occupies its own tile)
        this.cavalry = cavalry;
        this.combined = new CombinedPiece(cavalry.getCol(), cavalry.getRow(), absorber.isBlack(), absorber);
    }

    @Override
    public void execute() {
        previousCondition = board.getTurnCondition();

        // Remove the absorber from its source tile
        board.getTiles().get(oldX).get(oldY).setPiece(null);
        board.getPieces().remove(movingPiece);

        // Remove the cavalry from its tile and trigger its cleanup (removes CavalryAura)
        board.getTiles().get(newX).get(newY).setPiece(null);
        board.getPieces().remove(cavalry);
        cavalry.onCapture(movingPiece);

        // Place combined piece on the cavalry's tile
        combined.setCol(newX);
        combined.setRow(newY);
        board.getTiles().get(newX).get(newY).setPiece(combined);
        board.getPieces().add(combined);
        combined.onStart(board);

        board.setEnPassantTile(null);

        boolean frenzyCapture = BloodFrenzyPassive.isActive(board, movingPiece.isBlack());
        if (board instanceof MatchBoard && endsTurn() && !frenzyCapture) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        for (Aura aura : new ArrayList<>(board.getBoardAuras())) {
            aura.onLand(combined, newX, newY, board);
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
            aura.onUndoLand(combined, newX, newY, board);
        }

        // Remove combined piece
        board.getTiles().get(newX).get(newY).setPiece(null);
        board.getPieces().remove(combined);

        // Restore cavalry and its aura
        board.getTiles().get(newX).get(newY).setPiece(cavalry);
        board.getPieces().add(cavalry);
        cavalry.undoOnCapture(movingPiece);

        // Restore absorber
        board.getTiles().get(oldX).get(oldY).setPiece(movingPiece);
        movingPiece.setCol(oldX);
        movingPiece.setRow(oldY);
        movingPiece.setIsFirstMove(isFirstMove);
        board.getPieces().add(movingPiece);

        board.setEnPassantTile(enPassantTile);

        boolean frenzyCapture = BloodFrenzyPassive.isActive(board, movingPiece.isBlack());
        if (board instanceof MatchBoard && endsTurn() && !frenzyCapture) {
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        board.setTurnCondition(previousCondition);

        for (Piece piece : new ArrayList<>(board.getPieces())) {
            piece.undoPostMove(this, board);
        }

        board.untick();
    }
}
