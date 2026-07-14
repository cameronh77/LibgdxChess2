package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.war.strategy.StrategyQueen;

/**
 * Regular-mode move for {@link StrategyQueen}. Behaves exactly like a normal queen move
 * but transitions the queen into telegraph mode after execution.
 */
public class QueenRegularMove extends Move {

    public QueenRegularMove(StrategyQueen queen, int newX, int newY, Board board) {
        super(queen, newX, newY, board);
    }

    @Override
    public void execute() {
        super.execute();
        ((StrategyQueen) movingPiece).enterTelegraphMode();
    }

    @Override
    public void undo() {
        ((StrategyQueen) movingPiece).leaveTelegraphMode();
        super.undo();
    }
}
