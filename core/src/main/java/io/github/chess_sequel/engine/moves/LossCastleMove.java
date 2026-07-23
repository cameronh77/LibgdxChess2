package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.SlimeAura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;

/**
 * Move used by the {@link io.github.chess_sequel.engine.pieces.war.loss.LossCastle}.
 * After the castle slides to its new tile, a {@link SlimeAura} is placed on the tile it
 * vacated — enemies that step on it are slowed. The aura is removed on undo.
 */
public class LossCastleMove extends Move {

    private final SlimeAura hauntedTile;

    public LossCastleMove(Piece piece, int newX, int newY, Board board) {
        super(piece, newX, newY, board);
        this.hauntedTile = new SlimeAura(piece.getCol(), piece.getRow(), piece.isBlack());
    }

    @Override
    public void execute() {
        super.execute();
        board.addAura(hauntedTile);
    }

    @Override
    public void undo() {
        board.removeAura(hauntedTile);
        super.undo();
    }
}
