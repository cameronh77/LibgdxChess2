package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;

/**
 * Free pawn move granted by the {@link io.github.chess_sequel.engine.pieces.war.strategy.Commander}.
 * Moves the pawn normally but does not end the turn, letting the player still take their main action.
 */
public class CommanderPawnMove extends Move {

    public CommanderPawnMove(Piece pawn, int newX, int newY, Board board) {
        super(pawn, newX, newY, board);
    }

    @Override
    public boolean endsTurn() { return false; }
}
