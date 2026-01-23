package io.github.chess_sequel.engine.pieces;

public enum ChessClass {
    CLASSIC("classic"),
    GOBLIN("goblin");

    private final String type;

    ChessClass(String type){
        this.type = type;
    }

    // Custom method to get the type
    public String getType() {
        return this.type;
    }
}
