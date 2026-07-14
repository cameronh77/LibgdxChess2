package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.jsonTypes.Dialogue;
import io.github.chess_sequel.engine.jsonTypes.DialogueNode;

import java.util.ArrayList;

/** Lore bookcase — shows placeholder lore text each time the player interacts with it. Repeatable. */
public class RedBookcase extends Interactable {

    private final GameRun gameRun;
    private final int bookcaseId;

    public RedBookcase(GameRun gameRun, int col, int row, int bookcaseId) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.bookcaseId = bookcaseId;
        this.filePath = "red-bookshelf.png";
    }

    @Override
    public boolean isPassable() { return false; }

    @Override
    public void interaction() {
        Dialogue d = new Dialogue();
        d.entry = "lore";
        DialogueNode node = new DialogueNode();
        node.id = "lore";
        node.lines = new ArrayList<>();
        node.lines.add("Bookshelf " + bookcaseId + ", lore line 1.");
        node.lines.add("Bookshelf " + bookcaseId + ", lore line 2.");
        node.lines.add("Bookshelf " + bookcaseId + ", lore line 3.");
        d.nodes = new ArrayList<>();
        d.nodes.add(node);
        gameRun.startDialogue(d, "lore", null);
    }
}
