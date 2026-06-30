package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Pawn;
import io.github.chess_sequel.engine.powers.pieceAltering.Stuck;

/**
 * Goblin faction pawn. Moves identically to a standard Pawn but applies a {@link io.github.chess_sequel.engine.powers.pieceAltering.Stuck}
 * debuff (3 turns) to any piece it captures, preventing that piece from moving.
 */
public class Goblin extends Pawn {

    private Stuck stuck = new Stuck(3);
    public Goblin(int x, int y, boolean isBlack){
        super(x, y, isBlack, "goblin", ChessClass.GOBLIN);
    }

    @Override
    public String getDescription() { return "Moves like a Pawn. On capture, stuns the target for 3 turns — they cannot move."; }

    @Override
    public void onCapture(Piece piece){
        piece.getAlterMovePowers().add(stuck);
    }

    @Override
    public void undoOnCapture(Piece piece){
        piece.getAlterMovePowers().remove(stuck);
    }
}
