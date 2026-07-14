package io.github.chess_sequel.engine.jsonTypes;

import java.util.ArrayList;

public class RoomTypeData {
    public String sectionId;
    public String roomType;
    public int mapX;    // 0 = use section default
    public int mapY;
    public int combatX; // 0 = use section default
    public int combatY;
    public ArrayList<RoomVariant> variants;
    public ArrayList<EnemyData> enemies;
}
