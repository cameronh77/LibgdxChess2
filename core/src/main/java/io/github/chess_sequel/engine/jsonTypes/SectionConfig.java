package io.github.chess_sequel.engine.jsonTypes;

import java.util.ArrayList;

public class SectionConfig {
    public String sectionId;
    public String pathId;
    public int gridWidth;
    public int gridHeight;
    public int mapX;
    public int mapY;
    public int combatX;
    public int combatY;
    public String spawnRoomType;
    public int targetRooms;
    public ArrayList<GuaranteedRoomConfig> guaranteedRooms;
    public ArrayList<RoomPoolEntry> roomPool;
}
