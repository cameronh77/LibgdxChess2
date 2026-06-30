package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.jsonTypes.Dialogue;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.player.BotPlayer;

public class NPCPiece extends Interactable {

    private boolean hostile = true;

    private final GameRun gameRun;
    private final BotPlayer botPlayer;
    private final Dialogue dialogue;
    private String entryNode;

    private int col, row;

    public NPCPiece(BotPlayer botPlayer, GameRun gameRun, int col, int row, Dialogue dialogue) {
        this.botPlayer = botPlayer;
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.dialogue = dialogue;
        this.entryNode = dialogue != null ? dialogue.entry : null;
        this.filePath = botPlayer.getLeadPiece().getFilePath();
    }

    public int getCol() { return col; }
    public int getRow() { return row; }
    public void setCol(int col) { this.col = col; }
    public void setRow(int row) { this.row = row; }

    public void setEntryNode(String nodeId) {
        this.entryNode = nodeId;
    }

    public void startCombat() {
        gameRun.addMatchBoard(botPlayer);
    }

    public void removeFromMap(Board board) {
        if (board instanceof MapBoard) {
            ((MapBoard) board).removeLocation(this);
        }
        board.getTiles().get(col).get(row).setInteractable(null);
    }

    @Override
    public void interaction() {
        if (botPlayer.getDefeated()) return;
        if (dialogue != null && entryNode != null) {
            gameRun.startDialogue(dialogue, entryNode, this);
        } else if (hostile) {
            gameRun.addMatchBoard(botPlayer);
        }
    }
}
