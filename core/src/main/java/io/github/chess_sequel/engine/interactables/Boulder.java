package io.github.chess_sequel.engine.interactables;

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
