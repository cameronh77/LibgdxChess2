package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.player.Player;

public class AlterLayoutBoard extends Board{

    public AlterLayoutBoard(GameRun gameRun, int boardX, int boardY, Player player){
        super(boardX, boardY, player, null);

        for(Piece piece: player.getPieces()){
            addToBoard(piece);
        }

    }




}
