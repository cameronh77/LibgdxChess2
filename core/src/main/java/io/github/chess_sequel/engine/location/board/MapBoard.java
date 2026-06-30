package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.interactables.Boulder;
import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.interactables.MapEntrance;
import io.github.chess_sequel.engine.interactables.MapExit;
import io.github.chess_sequel.engine.interactables.NPCPiece;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.pieces.factories.ShopFactory;
import io.github.chess_sequel.engine.jsonTypes.EnemyData;
import io.github.chess_sequel.engine.jsonTypes.MapNode;
import io.github.chess_sequel.engine.jsonTypes.ZoneVariant;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;

import java.util.ArrayList;

/**
 * Exploration board used between combat encounters. Holds {@link Interactable} objects
 * (NPCs, shops, boulders, entrances, exits) at specific tile positions. Only the player's
 * king moves on this board.
 */
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

    /** Registers an interactable on the board and places it on the appropriate tile. */
    public void addLocation(Interactable location){
        locations.add(location);
        System.out.println("This is the col: " + location.getCol() + " This is the row: " + location.getRow());
        tiles.get(location.getCol()).get(location.getRow()).setInteractable(location);
    }

    /** Reads the variant's node list and instantiates all interactables for this map layout. */
    public void populateBoard(ZoneVariant variant, GameRun gameRun){
        pieces.clear();
        for(MapNode node: variant.nodes){
            switch(node.type){
                case "enemy":
                    EnemyData enemyData = gameRun.getJsonLoader().getEnemyData(gameRun.getCurrentMap(), node.ref);
                    BotPlayer botPlayer = new BotPlayer(gameRun, 3, enemyData.enemyLayout, enemyData.rewards);
                    addLocation(new NPCPiece(botPlayer, gameRun, node.x, node.y, enemyData.dialogue));
                    break;
                case "shopitem":
                    addLocation(ShopFactory.createShopItem(node, gameRun));
                    break;
                case "boulder":
                    addLocation(new Boulder(node.x, node.y));
                    break;
                case "entrance":
                    addLocation(new MapEntrance(gameRun, node.x, node.y, node.ref, node.icon));
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
