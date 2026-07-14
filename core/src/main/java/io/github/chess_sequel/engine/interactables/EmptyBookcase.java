package io.github.chess_sequel.engine.interactables;

/** Decorative impassable terrain — an empty bookcase with no readable content. */
public class EmptyBookcase extends Interactable {

    public EmptyBookcase(int col, int row) {
        this.col = col;
        this.row = row;
        this.filePath = "empty-bookshelf.png";
    }

    @Override
    public boolean isPassable() { return false; }

    @Override
    public void interaction() {}
}
