package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;

public class Exit extends Interactable{

    private GameRun game;

    public Exit(int col, int row, GameRun gameRun){
        this.col = col;
        this.row = row;
        this.filePath = "exit.png";
        this.game = gameRun;
    }

    @Override
    public void interaction(){
        game.popBoard();
    }
}
