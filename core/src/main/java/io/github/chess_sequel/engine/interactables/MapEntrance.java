package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;

public class MapEntrance extends Interactable {

    private final String zoneName;
    private final GameRun gameRun;

    public MapEntrance(GameRun gameRun, int col, int row, String zoneName) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.zoneName = zoneName;
        this.filePath = "door.png";
    }

    @Override
    public void interaction() {
        gameRun.pushMap(zoneName);
    }
}
