package io.github.chess_sequel.engine.interactables;

/**
 * An object that occupies a map tile and triggers an effect when the player's king moves
 * onto it. Subclasses define the specific interaction (combat, shop, travel, dialogue, etc.).
 * {@link #isPassable()} returns {@code false} for terrain that blocks movement.
 */
public abstract class Interactable {

    protected int col, row;
    protected String filePath;
    public abstract void interaction();

    public boolean isPassable() { return true; }

    public void setCol(int col){
        this.col = col;
    }
    public int getCol(){
        return col;
    }

    public void setRow(int row){
        this.row = row;
    }

    public int getRow(){
        return row;
    }

    public String getFilePath(){return filePath;}
}
