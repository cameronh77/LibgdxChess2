package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.interactables.Shop;
import io.github.chess_sequel.engine.interactables.ShopItem;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.player.Player;

import java.util.ArrayList;

public class ShopBoard extends Board{

    private Shop shop;
    private ArrayList<Interactable> locations = new ArrayList<>();
    public ShopBoard(int boardX, int boardY, Player player, Shop shop){
        super(boardX, boardY, player, null);
        this.shop = shop;
        fillShop(shop.getWares());
        addToBoard(player.getLeadPiece());
    }

    public void fillShop(ArrayList<ShopItem> wares){
        for(ShopItem ware: wares){
            addLocation(ware);
        }
    }

    public void addLocation(Interactable location){
        System.out.println("This is the col: " + location.getCol() + " This is the row: " + location.getRow());
        tiles.get(location.getCol()).get(location.getRow()).setInteractable(location);
        locations.add(location);
    }

    public ArrayList<Interactable> getWares(){
        return locations;
    }

    @Override
    public void addToBoard(Piece piece){
        pieces.add(piece);
        tiles.get(piece.getCol()).get(piece.getRow()).setPiece(piece);
    }

    public Shop getShop(){
        return shop;
    }

    @Override
    public BoardType getBoardType() { return BoardType.SHOP; }

}
