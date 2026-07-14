package io.github.chess_sequel.engine.hub;

import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.section.Direction;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HubRoom {
    public final String id;
    public final int mapX, mapY;
    /** Fixed spawn position used when entering the hub fresh (not via door navigation). -1 if unused. */
    public final int spawnCol, spawnRow;
    /** Section ID to enter when the portal in this room is used. Null if no portal. */
    public final String portalSectionId;
    public final Set<Direction> doors;
    public final Map<Direction, String> connections;
    public MapBoard board;
    public int kingExitCol = -1, kingExitRow = -1;

    public HubRoom(String id, int mapX, int mapY, int spawnCol, int spawnRow, String portalSectionId) {
        this.id = id;
        this.mapX = mapX;
        this.mapY = mapY;
        this.spawnCol = spawnCol;
        this.spawnRow = spawnRow;
        this.portalSectionId = portalSectionId;
        this.doors = EnumSet.noneOf(Direction.class);
        this.connections = new HashMap<>();
    }
}
