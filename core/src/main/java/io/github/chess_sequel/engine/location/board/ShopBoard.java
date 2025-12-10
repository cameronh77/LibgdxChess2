package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.player.Player;

public class ShopBoard extends Board{
    public ShopBoard(int boardX, int boardY, Player player){
        super(boardX, boardY, player, null);
    }
}
