package io.github.chess_sequel.engine;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;

import java.util.Stack;

public class Game {

    private Stack<Board> gameBoards = new Stack<>();
    private Player player;

    public Game(){
        addMatchBoard(new BotPlayer(3));
    }

    public void addMatchBoard(BotPlayer opponent){
        gameBoards.add(new MatchBoard(8, 8, opponent));
    }

    public void addShopBoard(){

    }

    public void addMapBoard(){

    }

    public Board getCurrentBoard(){
        return gameBoards.peek();
    }
}
