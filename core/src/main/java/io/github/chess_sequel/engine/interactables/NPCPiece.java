package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.player.BotPlayer;

public class NPCPiece extends Interactable{

    private Boolean hostile = true;

    private GameRun gameRun;
    private BotPlayer botPlayer;

    private int col, row;

    public NPCPiece(BotPlayer botPlayer, GameRun gameRun, int col, int row){
        this.botPlayer = botPlayer;
        this.gameRun = gameRun;
        this.col = col;
        this.row = row;
        this.filePath = botPlayer.getLeadPiece().getFilePath();
    }

    public int getCol(){
        return col;
    }

    public int getRow(){
        return row;
    }

    public void setCol(int col){
        this.col = col;
    }

    public void setRow(int row){
        this.row = row;
    }

    public void createArmy(){

    }

    @Override
    public void interaction(){
        if(hostile && !botPlayer.getDefeated()){
            gameRun.addMatchBoard(botPlayer);
        }

    }
}
