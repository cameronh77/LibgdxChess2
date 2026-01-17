package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;

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
