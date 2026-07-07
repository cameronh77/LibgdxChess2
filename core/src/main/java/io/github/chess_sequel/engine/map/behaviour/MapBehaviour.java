package io.github.chess_sequel.engine.map.behaviour;

import io.github.chess_sequel.engine.interactables.NPCPiece;
import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.location.board.MapBoard;

/**
 * Strategy for how an NPCPiece moves on the map each time the player takes a step.
 * Subclasses define the movement pattern; shared tile-movement logic lives here.
 */
public abstract class MapBehaviour {

    public abstract void tick(NPCPiece npc, MapBoard board);

    /**
     * Attempts to move the NPC to (col, row). If the king is there, triggers interaction
     * (dialogue or combat) without physically occupying the tile. Returns true if the NPC moved.
     */
    protected boolean tryMoveTo(NPCPiece npc, int col, int row, MapBoard board) {
        if (col < 0 || col >= board.boardX || row < 0 || row >= board.boardY) return false;
        Tile dest = board.getTiles().get(col).get(row);
        if (dest.getPiece() != null) {
            npc.interaction();
            return false;
        }
        if (dest.getInteractable() != null) return false;
        board.getTiles().get(npc.getCol()).get(npc.getRow()).setInteractable(null);
        npc.setCol(col);
        npc.setRow(row);
        dest.setInteractable(npc);
        return true;
    }
}
