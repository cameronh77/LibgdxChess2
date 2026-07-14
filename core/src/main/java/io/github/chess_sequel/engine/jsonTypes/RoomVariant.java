package io.github.chess_sequel.engine.jsonTypes;

import java.util.ArrayList;

public class RoomVariant {
    public String variantId;
    public ArrayList<MapNode> nodes;
    // Which sides of this room are physically clear for a door. null = any direction is supported.
    public ArrayList<String> possibleDoors;
}
