package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;

/** The throne in the hub. Interacting with it opens king/team selection. */
public class Throne extends Interactable {

    private final GameRun gameRun;

    public Throne(GameRun gameRun, int col, int row) {
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.filePath = "throne.png";
    }

    @Override
    public void interaction() {
        gameRun.setGameState(GameState.GO_TO_KING_SELECTION);
    }
}
