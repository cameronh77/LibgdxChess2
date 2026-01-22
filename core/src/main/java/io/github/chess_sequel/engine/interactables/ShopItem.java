package io.github.chess_sequel.engine.interactables;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.location.board.ShopBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.player.Player;

public class ShopItem extends Interactable{

    private int price;
    private Piece piece;
    private GameRun game;

    public ShopItem(int col, int row, int price, Piece piece, GameRun game){
        this.col = col;
        this.row = row;
        this.filePath = piece.getFilePath();
        this.piece = piece;
        this.game = game;
        this.price = price;
    }

    @Override
    public void interaction(){
        if(game.getPlayer().getCurrency() >= price){
            game.getPlayer().getPieceInventory().add(piece);
            ShopBoard board = (ShopBoard) game.getCurrentBoard();
            board.getTiles().get(col).get(row).setInteractable(null);
            board.getWares().remove(this);
            game.getPlayer().decrementCurrency(price);
            board.getShop().getWares().remove(this);
            game.setGameState(GameState.CHANGING_INVENTORY);
        }
    }

    public int getPrice(){
        return price;
    }
}
