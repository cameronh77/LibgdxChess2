package io.github.chess_sequel.engine.map.behaviour;

import io.github.chess_sequel.engine.interactables.NPCPiece;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.pieces.Piece;

/**
 * Moves the NPC one step away from the player's king each turn.
 * Stays put if cornered on all escape sides.
 */
public class FleeBehaviour extends MapBehaviour {

    @Override
    public void tick(NPCPiece npc, MapBoard board) {
        if (board.getPieces().isEmpty()) return;
        Piece king = board.getPieces().get(0);
        int dc = -Integer.signum(king.getCol() - npc.getCol());
        int dr = -Integer.signum(king.getRow() - npc.getRow());
        if (dc != 0 && !tryMoveTo(npc, npc.getCol() + dc, npc.getRow(), board)) {
            tryMoveTo(npc, npc.getCol(), npc.getRow() + dr, board);
        } else if (dc == 0) {
            tryMoveTo(npc, npc.getCol(), npc.getRow() + dr, board);
        }
    }
}
