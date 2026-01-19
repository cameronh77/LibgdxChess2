package io.github.chess_sequel.engine.jsonTypes;

import java.util.ArrayList;

public class MapData {
    public String mapName;
    public int mapX;
    public int mapY;
    public ArrayList<String> mapLayouts;
    public ArrayList<String> locationInternals;

    public ArrayList<Coordinates> startingPositions;
}

