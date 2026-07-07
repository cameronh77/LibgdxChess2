package io.github.chess_sequel.engine.map.behaviour;

import io.github.chess_sequel.engine.interactables.NPCPiece;
import io.github.chess_sequel.engine.location.board.MapBoard;

/**
 * Cycles the NPC through a fixed list of waypoints in order, looping back to the first.
 * Takes one greedy step per player move, preferring horizontal movement when diagonal.
 */
public class PatrolBehaviour extends MapBehaviour {

    private final int[][] waypoints;
    private int current = 0;

    public PatrolBehaviour(int[][] waypoints) {
        this.waypoints = waypoints;
    }

    @Override
    public void tick(NPCPiece npc, MapBoard board) {
        if (waypoints == null || waypoints.length == 0) return;
        int targetCol = waypoints[current][0];
        int targetRow = waypoints[current][1];
        if (npc.getCol() == targetCol && npc.getRow() == targetRow) {
            current = (current + 1) % waypoints.length;
            targetCol = waypoints[current][0];
            targetRow = waypoints[current][1];
        }
        int dc = Integer.signum(targetCol - npc.getCol());
        int dr = Integer.signum(targetRow - npc.getRow());
        if (dc != 0 && !tryMoveTo(npc, npc.getCol() + dc, npc.getRow(), board)) {
            tryMoveTo(npc, npc.getCol(), npc.getRow() + dr, board);
        } else if (dc == 0) {
            tryMoveTo(npc, npc.getCol(), npc.getRow() + dr, board);
        }
    }
}
