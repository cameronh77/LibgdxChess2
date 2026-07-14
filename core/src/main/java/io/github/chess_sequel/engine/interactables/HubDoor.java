package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.section.Direction;

/** Edge-of-room interactable that navigates between hub rooms. */
public class HubDoor extends Interactable {

    private final GameRun gameRun;
    private final Direction direction;

    public HubDoor(GameRun gameRun, int col, int row, Direction direction) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.direction = direction;
        this.filePath = "door.png";
    }

    @Override
    public void interaction() {
        gameRun.navigateHubRoom(direction);
    }
}
