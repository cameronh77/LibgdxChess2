package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;

/**
 * Reward-unlocked portal that permanently transitions the run to a new level zone.
 * Unlike {@link MapEntrance}, this replaces the current map rather than pushing a sub-zone.
 */
public class LevelPortal extends Interactable{

    private String level;
    private GameRun gameRun;

    public LevelPortal(String level, GameRun gameRun, int col, int row){
        this.level = level;
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.filePath = "level-portal.png";
    }


    public void interaction(){
        gameRun.progressGame(level);
    };

}
