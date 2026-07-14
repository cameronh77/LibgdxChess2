package io.github.chess_sequel.engine.save;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Cross-run meta-progression. Survives individual run deaths/completions.
 * Drives hub content — which NPCs are present, which quests are active, etc.
 */
public class PersistentData {
    public int selectedKingIndex  = 0;
    public int selectedPresetIndex = 0;
    public ArrayList<String> clearedSections = new ArrayList<>();
    public ArrayList<String> defeatedEnemies  = new ArrayList<>();
    public ArrayList<String> visitedLocations = new ArrayList<>();
    public ArrayList<String> unlocks          = new ArrayList<>();
    public HashMap<String, NpcQuestState> npcStates = new HashMap<>();

    public boolean hasClearedSection(String id)  { return clearedSections.contains(id); }
    public boolean hasDefeated(String id)         { return defeatedEnemies.contains(id); }
    public boolean hasVisited(String id)          { return visitedLocations.contains(id); }
    public boolean hasUnlock(String key)          { return unlocks.contains(key); }

    public NpcQuestState getOrCreateNpcState(String npcId) {
        if (!npcStates.containsKey(npcId)) npcStates.put(npcId, new NpcQuestState());
        return npcStates.get(npcId);
    }

    public int getNpcStep(String npcId) {
        NpcQuestState s = npcStates.get(npcId);
        return s == null ? 0 : s.step;
    }
}
