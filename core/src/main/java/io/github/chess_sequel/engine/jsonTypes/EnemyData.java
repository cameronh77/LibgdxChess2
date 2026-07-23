package io.github.chess_sequel.engine.jsonTypes;

import java.util.ArrayList;

public class EnemyData {
    public String enemyId;
    public boolean fixed;
    public ArrayList<PiecePlacement> enemyLayout;
    public ArrayList<String> powers; // power IDs applied to the lead piece (first piece in enemyLayout)
    public Dialogue dialogue;
    public Rewards rewards;
    public String defeatBehaviour; // "remove" (default), "dialogue", "stay"
    public Dialogue defeatDialogue; // used when defeatBehaviour is "dialogue"
}
