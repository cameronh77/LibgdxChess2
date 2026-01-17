package io.github.chess_sequel.engine.jsonTypes;

import java.util.ArrayList;

public class MapEnemies {
    public String mapName;
    public ArrayList<EnemyData> enemies;
    public ArrayList<String> randomEnemies; //A list of enemy Ids that can appear in a random encounter;
}
