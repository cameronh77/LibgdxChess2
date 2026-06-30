package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;

/** Door-like interactable that pushes the player into a named sub-zone via {@link io.github.chess_sequel.engine.GameRun#pushMap}. */
public class MapEntrance extends Interactable {

    private final String zoneName;
    private final GameRun gameRun;

    public MapEntrance(GameRun gameRun, int col, int row, String zoneName, String icon) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.zoneName = zoneName;
        this.filePath = icon != null ? icon : "door.png";
    }

    @Override
    public void interaction() {
        gameRun.pushMap(zoneName);
    }
}
