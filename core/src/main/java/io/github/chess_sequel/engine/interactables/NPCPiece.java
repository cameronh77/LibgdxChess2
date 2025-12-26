package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.Game;
import io.github.chess_sequel.engine.pieces.PieceType;
import io.github.chess_sequel.engine.player.BotPlayer;

public class NPCPiece extends Interactable{

    private Boolean hostile = true;

    private Game game;
    private BotPlayer botPlayer;

    private int col, row;

    public NPCPiece(BotPlayer botPlayer, Game game, int col, int row){
        System.out.println("test");
        this.botPlayer = botPlayer;
        this.game = game;
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
        if(hostile){
            game.addMatchBoard(botPlayer);
        }

    }
}
