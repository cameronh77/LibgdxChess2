package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.jsonTypes.Dialogue;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.map.behaviour.MapBehaviour;
import io.github.chess_sequel.engine.player.BotPlayer;

/**
 * Map interactable representing an enemy (or neutral NPC) piece. Approaching triggers
 * dialogue or combat. On defeat, behaviour is controlled by {@code defeatBehaviour}:
 *   "remove"   — disappears immediately when you return to the map after winning.
 *   "dialogue" — stays until approached; plays defeatDialogue then disappears.
 *   "stay"     — becomes passable; you can walk through the tile.
 */
public class NPCPiece extends Interactable {

    private boolean hostile = true;

    private final GameRun gameRun;
    private final BotPlayer botPlayer;
    private final Dialogue dialogue;
    private String entryNode;
    private MapBehaviour behaviour;

    private final String npcId;
    private final String defeatBehaviour;
    private final Dialogue defeatDialogue;

    private int col, row;

    public NPCPiece(BotPlayer botPlayer, GameRun gameRun, int col, int row,
                    Dialogue dialogue, String npcId, String defeatBehaviour, Dialogue defeatDialogue) {
        this.botPlayer = botPlayer;
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.dialogue = dialogue;
        this.entryNode = dialogue != null ? dialogue.entry : null;
        this.npcId = npcId;
        this.defeatBehaviour = defeatBehaviour != null ? defeatBehaviour : "remove";
        this.defeatDialogue = defeatDialogue;
        this.filePath = botPlayer.getLeadPiece().getFilePath();
    }

    public int getCol() { return col; }
    public int getRow() { return row; }
    public void setCol(int col) { this.col = col; }
    public void setRow(int row) { this.row = row; }

    public BotPlayer getBotPlayer() { return botPlayer; }
    public String getDefeatBehaviour() { return defeatBehaviour; }

    public void setBehaviour(MapBehaviour behaviour) { this.behaviour = behaviour; }

    public void onTick(MapBoard board) {
        if (botPlayer.getDefeated()) {
            behaviour = null;
            return;
        }
        if (behaviour != null) behaviour.tick(this, board);
    }

    public void setEntryNode(String nodeId) {
        this.entryNode = nodeId;
    }

    /** Directly starts a match against this NPC's bot player, bypassing dialogue. */
    public void startCombat() {
        gameRun.addMatchBoard(botPlayer);
    }

    /** True only for a defeated "stay" NPC — all others block movement. */
    @Override
    public boolean isPassable() {
        return botPlayer.getDefeated() && "stay".equals(defeatBehaviour);
    }

    public void removeFromMap(Board board) {
        if (npcId != null) {
            gameRun.flagConditionMet("defeat", npcId);
        }
        if (board instanceof MapBoard) {
            ((MapBoard) board).removeLocation(this);
        }
        board.getTiles().get(col).get(row).setInteractable(null);
    }

    /** Returns true if this NPC should be swept from the map immediately after combat. */
    public boolean shouldRemoveOnDefeat() {
        return botPlayer.getDefeated() && "remove".equals(defeatBehaviour);
    }

    @Override
    public void interaction() {
        if (botPlayer.getDefeated()) {
            if (npcId != null) gameRun.flagConditionMet("defeat", npcId);
            if ("dialogue".equals(defeatBehaviour) && defeatDialogue != null) {
                gameRun.startDialogue(defeatDialogue, defeatDialogue.entry, this);
            }
            return;
        }
        if (dialogue != null && entryNode != null) {
            gameRun.startDialogue(dialogue, entryNode, this);
        } else if (hostile) {
            gameRun.addMatchBoard(botPlayer);
        }
    }
}
