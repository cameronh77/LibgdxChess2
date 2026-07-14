package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.section.Direction;

/** Edge-of-room interactable that navigates to the adjacent section room in the given direction. */
public class RoomDoor extends Interactable {

    private final GameRun gameRun;
    private final Direction direction;

    public RoomDoor(GameRun gameRun, int col, int row, Direction direction) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.direction = direction;
        this.filePath = "door.png";
    }

    @Override
    public void interaction() {
        gameRun.navigateRoom(direction);
    }
}
