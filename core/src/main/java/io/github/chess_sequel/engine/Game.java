package io.github.chess_sequel.engine;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;


import java.util.Stack;

public class Game {

    private Stack<Board> gameBoards = new Stack<>();
    private Player player;

    public Game(Player player){
        this.player = player;
        //addMatchBoard(new BotPlayer(3));
        addMapBoard();
    }

    public void addMatchBoard(BotPlayer opponent){
        gameBoards.push(new MatchBoard(8, 8, player, opponent));
    }

    public void addShopBoard(){

    }

    public void addMapBoard(){
        gameBoards.push(new MapBoard(this, 8, 8, player, "e00 e06"));
    }

    public Board getCurrentBoard(){
        return gameBoards.peek();
    }
}
