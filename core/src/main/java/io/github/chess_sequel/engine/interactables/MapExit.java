package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;

/** Exit tile that returns the player to the parent map via {@link io.github.chess_sequel.engine.GameRun#popMap}. */
public class MapExit extends Interactable {

    private final GameRun gameRun;

    public MapExit(GameRun gameRun, int col, int row) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.filePath = "exit.png";
    }

    @Override
    public void interaction() {
        gameRun.popMap();
    }
}
