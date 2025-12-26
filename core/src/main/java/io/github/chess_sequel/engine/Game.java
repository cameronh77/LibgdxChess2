package io.github.chess_sequel.engine;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;


import java.util.ArrayList;
import java.util.Stack;

public class Game {

    private Stack<Board> gameBoards = new Stack<>();
    private Player player;
    private JsonLoader jsonLoader = new JsonLoader();
    private String currentMap;

    public Game(Player player){
        this.player = player;
        jsonLoader.loadMapData();
        jsonLoader.loadEnemyData();
        this.currentMap = "second";
        addMapBoard();
    }

    public void addMatchBoard(BotPlayer opponent){
        gameBoards.push(new MatchBoard(8, 8, player, opponent));
    }

    public void addShopBoard(){

    }

    public void addMapBoard(){
        ArrayList<String> layouts = jsonLoader.getMapData(currentMap).mapLayouts;
        ArrayList<String> internalLayouts = jsonLoader.getMapData(currentMap).locationInternals;
        int randomIndex = (int)(Math.random() * layouts.size());
        String layout = layouts.get(randomIndex);
        String internalLayout = internalLayouts.get(randomIndex);
        gameBoards.push(new MapBoard(this, 8, 8, player, layout, internalLayout));
    }

    public Board getCurrentBoard(){
        return gameBoards.peek();
    }

    public String getCurrentMap(){
        return currentMap;
    }

    public JsonLoader getJsonLoader(){
        return jsonLoader;
    }
}
