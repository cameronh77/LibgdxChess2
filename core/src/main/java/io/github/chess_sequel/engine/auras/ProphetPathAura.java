package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Board-level aura marking a future tile in the {@link io.github.chess_sequel.engine.pieces.war.strategy.ProphetBishop}'s
 * committed path. Purely visual — shown to both players.
 */
public class ProphetPathAura extends Aura {

    public ProphetPathAura(Piece owner, int col, int row) {
        super(owner, "prophetPathAura");
        this.auraCol = col;
        this.auraRow = row;
        this.imagePath = "tileModifiers/prophet-path.png";
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        return moves;
    }
}
