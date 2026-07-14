package io.github.chess_sequel.engine.section;

import io.github.chess_sequel.engine.location.board.MapBoard;

import java.util.EnumSet;
import java.util.Set;

public class RoomNode {
    public final int gridX;
    public final int gridY;
    public final String section;
    public String type;
    public boolean revealed;
    public boolean visited;
    public Set<Direction> doors;
    public MapBoard board;
    public int kingExitCol = -1;
    public int kingExitRow = -1;
    public int mapX;
    public int mapY;
    public int combatX;
    public int combatY;

    public RoomNode(int gridX, int gridY, String section, String type) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.section = section;
        this.type = type;
        this.doors = EnumSet.noneOf(Direction.class);
        this.revealed = false;
        this.visited = false;
    }
}
