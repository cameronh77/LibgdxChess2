package io.github.chess_sequel.engine;

import io.github.chess_sequel.engine.interactables.Exit;
import io.github.chess_sequel.engine.interactables.LevelPortal;
import io.github.chess_sequel.engine.interactables.Shop;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.jsonTypes.Rewards;
import io.github.chess_sequel.engine.location.board.*;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;


import java.util.ArrayList;
import java.util.Stack;

public class GameRun {

    private Stack<Board> gameBoards = new Stack<>();
    private Player player;
    private JsonLoader jsonLoader = new JsonLoader();
    private String currentMap;
    private GameState gameState = GameState.NEUTRAL;

    public GameRun(Player player){
        this.player = player;
        jsonLoader.loadMapData();
        jsonLoader.loadEnemyData();
        jsonLoader.loadShopData();
        this.currentMap = "classic4";
        jsonLoader.setMapSizeXY(currentMap);
        addMapBoard();
    }

    public void addMatchBoard(BotPlayer opponent){
        System.out.println("Adding match board");
        player.setLeadPieceX(player.getLeadPiece().getCol());
        player.setLeadPieceY(player.getLeadPiece().getRow());
        gameBoards.push(new MatchBoard(jsonLoader.getMapSizeX(), jsonLoader.getMapSizeY(), player, opponent));
    }

    public void addShopBoard(Shop shop, int xOrd, int yOrd){
        System.out.println("Adding shop board");
        player.setLeadPieceX(player.getLeadPiece().getCol());
        player.setLeadPieceY(player.getLeadPiece().getRow());

        player.getLeadPiece().setCol(xOrd);
        player.getLeadPiece().setRow(yOrd);


        ShopBoard shopBoard = new ShopBoard(jsonLoader.getMapSizeX(), jsonLoader.getMapSizeY(), player, shop);
        shopBoard.addLocation(new Exit(xOrd, yOrd, this));
        gameBoards.push(shopBoard);

    }

    public void handleRewards(Rewards rewards){
        MapBoard mapBoard = (MapBoard) getCurrentBoard();
        if(rewards.portals != null){
            for(int p = 0; p<rewards.portals.size();p++){
                mapBoard.addLocation(new LevelPortal(rewards.portals.get(p), this, rewards.portalLocations.get(p).x, rewards.portalLocations.get(p).y));
            }
        }
        if(rewards.currency != null){
            player.incrementCurrency(rewards.currency);
        }
    }

    public void progressGame(String level){
        //this.currentMap = level.getLevel();
        gameState = GameState.CHANGING_MAP;
        this.currentMap = level;
        jsonLoader.setMapSizeXY(currentMap);
        popBoard();
        addMapBoard();
    }

    public void addMapBoard(){
        ArrayList<String> layouts = jsonLoader.getMapData(currentMap).mapLayouts;
        ArrayList<String> internalLayouts = jsonLoader.getMapData(currentMap).locationInternals;
        int randomIndex = (int)(Math.random() * layouts.size());
        String layout = layouts.get(randomIndex);
        String internalLayout = internalLayouts.get(randomIndex);
        player.setLeadPieceX(jsonLoader.getMapData(currentMap).startingPositions.get(randomIndex).x);
        player.setLeadPieceY(jsonLoader.getMapData(currentMap).startingPositions.get(randomIndex).y);

        //player.getLeadPiece().setCol(player.getLeadPieceX());
        //player.getLeadPiece().setRow(player.getLeadPieceY());

        gameBoards.push(new MapBoard(this, jsonLoader.getMapSizeX(), jsonLoader.getMapSizeY(), player, layout, internalLayout));
    }

    public Board getCurrentBoard(){
        return gameBoards.peek();
    }
    public void popBoard(){
        gameBoards.pop();
        player.getLeadPiece().setCol(player.getLeadPieceX());
        player.getLeadPiece().setRow(player.getLeadPieceY());
    }

    public void alterLayout(){
        if(gameBoards.peek() instanceof AlterLayoutBoard){
            popBoard();
            player.getLeadPiece().setCol(player.getLeadPieceX());
            player.getLeadPiece().setRow(player.getLeadPieceY());
        }
        else{
            player.setLeadPieceX(player.getLeadPiece().getCol());
            player.setLeadPieceY(player.getLeadPiece().getRow());
            gameBoards.push(new AlterLayoutBoard(this, jsonLoader.getMapSizeX(), jsonLoader.getMapSizeY(), player));
        }
    }

    public String getCurrentMap(){
        return currentMap;
    }

    public JsonLoader getJsonLoader(){
        return jsonLoader;
    }

    public void setGameState(GameState gameState){
        this.gameState = gameState;
    }

    public GameState getGameState(){
        return gameState;
    }

    public Player getPlayer(){
        return player;
    }

}
