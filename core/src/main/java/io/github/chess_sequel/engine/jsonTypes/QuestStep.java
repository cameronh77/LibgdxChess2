package io.github.chess_sequel.engine.jsonTypes;

/**
 * One step in an NPC's quest. The NPC stays on this step until its condition
 * is met, at which point they acknowledge it on next conversation, give the
 * reward, and advance to the next step.
 */
public class QuestStep {
    /** Where this NPC lives during this step: "hub", "strategy", "war", etc. */
    public String location;
    /** What the player must do before the NPC will advance. */
    public QuestCondition condition;
    /** Dialogue shown while the condition is unmet. */
    public Dialogue pendingDialogue;
    /** Dialogue shown when the condition is met but reward not yet claimed. */
    public Dialogue readyDialogue;
    /** Reward given when readyDialogue ends. Null = no reward. */
    public Rewards reward;
}
