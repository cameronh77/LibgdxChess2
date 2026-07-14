package io.github.chess_sequel.engine.section;

public class SectionLayout {
    public String sectionId;
    public RoomNode[][] grid;
    public RoomNode spawnRoom;
    public RoomNode bossRoom;
    public RoomNode currentRoom;

    public RoomNode getRoomInDirection(Direction dir) {
        int x = currentRoom.gridX;
        int y = currentRoom.gridY;
        switch (dir) {
            case EAST:  x++; break;
            case WEST:  x--; break;
            case NORTH: y++; break;
            case SOUTH: y--; break;
        }
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) return null;
        return grid[x][y];
    }
}
