package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.player.Player;

/**
 * Pre-combat team-arrangement board. Shows the player's pieces in the bottom half of the
 * grid so they can be repositioned before a match begins. Drag-and-drop from the inventory
 * panel also targets this board.
 */
public class AlterLayoutBoard extends Board{

    public AlterLayoutBoard(GameRun gameRun, int boardX, int boardY, Player player){
        super(boardX, boardY, player, null);

        for(Piece piece: player.getPieces()){
            addToBoard(piece);
        }

    }

    @Override
    public BoardType getBoardType() { return BoardType.ALTER_LAYOUT; }

}
