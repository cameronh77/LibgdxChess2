package io.github.chess_sequel.engine.jsonTypes;

import java.util.ArrayList;

public class ZoneData {
    public String mapName;
    public int mapX;
    public int mapY;
    public ArrayList<ZoneVariant> variants;
    public ArrayList<EnemyData> enemies;
    public ArrayList<ShopData> shops;
}
