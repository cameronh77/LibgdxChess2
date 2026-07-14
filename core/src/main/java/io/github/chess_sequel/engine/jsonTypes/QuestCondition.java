package io.github.chess_sequel.engine.jsonTypes;

/**
 * Condition that must be met before a quest step's reward is given and the
 * NPC advances to the next step.
 *
 * type values: "none", "clearSection", "defeat", "goTo", "talkTo", "giveItem"
 * target: the ID that the type refers to (section ID, enemy ID, location ID, etc.)
 */
public class QuestCondition {
    public String type   = "none";
    public String target = "";
}
