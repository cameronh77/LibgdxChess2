package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.jsonTypes.Coordinates;
import io.github.chess_sequel.engine.jsonTypes.IndividualWare;
import io.github.chess_sequel.engine.pieces.classic.Pawn;
import io.github.chess_sequel.engine.pieces.factories.ShopFactory;

import java.util.ArrayList;

public class Shop extends Interactable{

    private ArrayList<ShopItem> wares = new ArrayList<>();
    private GameRun game;
    private int entranceX;
    private int entranceY;


    public Shop(GameRun game, int col, int row, ArrayList<IndividualWare> waresArray, Coordinates coordinates){
        this.col = col;
        this.row = row;
        this.game = game;
        this.filePath = "shop.png";
        processWares(waresArray);
        entranceX = coordinates.x;
        entranceY = coordinates.y;
    }

    @Override
    public void interaction(){
        game.addShopBoard(this, entranceX, entranceY);
    }

    public ArrayList<ShopItem> getWares(){
        return wares;
    }

    public GameRun getGame(){
        return game;
    }

    public void processWares(ArrayList<IndividualWare> waresArray){
        for(IndividualWare ware: waresArray){
            wares.add(ShopFactory.createShopItem(ware, game));

        }
    }
}
