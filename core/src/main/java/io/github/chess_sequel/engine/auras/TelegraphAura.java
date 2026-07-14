package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Board-level aura marking the {@link io.github.chess_sequel.engine.pieces.war.strategy.StrategyQueen}'s
 * telegraphed destination. Purely visual — shown to both players so the opponent can react.
 */
public class TelegraphAura extends Aura {

    public TelegraphAura(Piece owner, int targetCol, int targetRow) {
        super(owner, "telegraphAura");
        this.auraCol = targetCol;
        this.auraRow = targetRow;
        this.imagePath = "tileModifiers/telegraph.png";
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        return moves;
    }
}
