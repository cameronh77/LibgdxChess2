package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.interactables.Boulder;
import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.interactables.MapEntrance;
import io.github.chess_sequel.engine.interactables.MapExit;
import io.github.chess_sequel.engine.interactables.NPCPiece;
import io.github.chess_sequel.engine.interactables.Shop;
import io.github.chess_sequel.engine.jsonTypes.EnemyData;
import io.github.chess_sequel.engine.jsonTypes.MapNode;
import io.github.chess_sequel.engine.jsonTypes.ShopData;
import io.github.chess_sequel.engine.jsonTypes.ZoneVariant;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;

import java.util.ArrayList;

public class MapBoard extends Board{


    private ArrayList<Interactable> locations = new ArrayList<>();

    public MapBoard(GameRun gameRun, int boardX, int boardY, Player player, ZoneVariant variant){
        super(boardX, boardY, player, null);
        populateBoard(variant, gameRun);

        pieces.add(player.getLeadPiece());
        player.getLeadPiece().setCol(player.getLeadPieceX());
        player.getLeadPiece().setRow(player.getLeadPieceY());
        tiles.get(player.getLeadPieceX()).get(player.getLeadPieceY()).setPiece(player.getLeadPiece());
    }

    public void addLocation(Interactable location){
        locations.add(location);
        System.out.println("This is the col: " + location.getCol() + " This is the row: " + location.getRow());
        tiles.get(location.getCol()).get(location.getRow()).setInteractable(location);
    }

    public void populateBoard(ZoneVariant variant, GameRun gameRun){
        pieces.clear();
        for(MapNode node: variant.nodes){
            switch(node.type){
                case "enemy":
                    EnemyData enemyData = gameRun.getJsonLoader().getEnemyData(gameRun.getCurrentMap(), node.ref);
                    BotPlayer botPlayer = new BotPlayer(gameRun, 3, enemyData.enemyLayout, enemyData.rewards);
                    addLocation(new NPCPiece(botPlayer, gameRun, node.x, node.y, enemyData.dialogue));
                    break;
                case "shop":
                    ShopData shopData = gameRun.getJsonLoader().getShopData(gameRun.getCurrentMap(), node.ref);
                    addLocation(new Shop(gameRun, node.x, node.y, shopData.shopLayout, shopData.startingCoords));
                    break;
                case "boulder":
                    addLocation(new Boulder(node.x, node.y));
                    break;
                case "entrance":
                    addLocation(new MapEntrance(gameRun, node.x, node.y, node.ref));
                    break;
                case "exit":
                    addLocation(new MapExit(gameRun, node.x, node.y));
                    break;
            }
        }
    }

    public ArrayList<Interactable> getLocations(){
        return locations;
    }

    public void removeLocation(Interactable location){
        locations.remove(location);
    }

    @Override
    public BoardType getBoardType() { return BoardType.MAP; }

}
