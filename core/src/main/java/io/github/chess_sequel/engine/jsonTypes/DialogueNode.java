package io.github.chess_sequel.engine.jsonTypes;

import java.util.ArrayList;

public class DialogueNode {
    public String id;
    public ArrayList<String> lines;
    public ArrayList<DialogueChoice> choices;
    public String outcome;   // "combat", "leave", or null
    public String setEntry;  // shifts the entry pointer for next interaction
    public Rewards reward;   // applied when this node resolves
}
