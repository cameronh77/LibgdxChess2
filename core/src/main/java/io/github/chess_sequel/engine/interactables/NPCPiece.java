package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.Game;
import io.github.chess_sequel.engine.pieces.PieceType;
import io.github.chess_sequel.engine.player.BotPlayer;

public class NPCPiece extends Interactable{

    private Boolean hostile = true;
    private PieceType displayPiece;
    private Game game;
    private BotPlayer botPlayer;

    private int col, row;

    public NPCPiece(BotPlayer botPlayer, Game game, PieceType displayPiece){
        this.botPlayer = botPlayer;
        this.game = game;
        this.displayPiece = displayPiece;
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

    @Override
    public void interaction(){
        if(hostile){
            game.addMatchBoard(botPlayer);
        }

    }
}
