package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Diagonal capture move for the Barbarian pawn. When it results in a capture the turn does
 * not flip — instead a TurnCondition is set that marks every other friendly piece as having
 * already acted, so only the Barbarian may move again. Chaining continues as long as the
 * Barbarian keeps capturing; any non-capture move ends the extra turn normally.
 */
public class BarbarianCapture extends Move {

    public BarbarianCapture(Piece piece, int newX, int newY, Board board) {
        super(piece, newX, newY, board);
    }

    @Override
    public boolean endsTurn() {
        return capturedPiece == null;
    }

    @Override
    public void execute() {
        super.execute();

        if (capturedPiece != null) {
            TurnCondition c = new TurnCondition(movingPiece.isBlack());
            for (Piece p : new ArrayList<>(board.getPieces())) {
                if (p != movingPiece && p.isBlack() == movingPiece.isBlack()) {
                    c = c.withActor(p);
                }
            }
            board.setTurnCondition(c);
        }
    }
}
