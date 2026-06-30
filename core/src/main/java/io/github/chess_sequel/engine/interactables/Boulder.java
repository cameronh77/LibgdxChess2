package io.github.chess_sequel.engine.interactables;

/** Impassable terrain on the map. The king cannot move through or onto a boulder tile. */
public class Boulder extends Interactable {

    public Boulder(int col, int row) {
        this.col = col;
        this.row = row;
        this.filePath = "tileModifiers/boulder.png";
    }

    @Override
    public boolean isPassable() { return false; }

    @Override
    public void interaction() {}
}
