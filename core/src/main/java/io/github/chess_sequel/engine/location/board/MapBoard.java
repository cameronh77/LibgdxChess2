package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.pieces.*;
import io.github.chess_sequel.engine.player.Player;

import java.util.ArrayList;

public class MapBoard extends Board{


    private ArrayList<Interactable> locations = new ArrayList<>();

    public MapBoard(int boardX, int boardY, Player player, String boardLayout){
        super(boardX, boardY, player, null);

        addToBoard(player.getLeadPiece());
        populateBoard(boardLayout);
    }

    public void addLocation(Interactable location){
        locations.add(location);
        tiles.get(location.getCol()).get(location.getRow()).setInteractable(location);
    }

    public void populateBoard(String boardLayout){
        pieces.clear();
        String[] parts = boardLayout.split(" ");

        //set up pieces
        String position = parts[0];
        int row = 0;
        int col = 0;
        for (int i = 0; i <position.length(); i++){
            char ch = position.charAt(i);
            if(ch == '/'){
                row++;
                col = 0;
            } else if (Character.isDigit(ch)) {
                col += Character.getNumericValue(ch);
            } else {
                boolean isWhite = Character.isUpperCase(ch);
                char pieceChar = Character.toLowerCase(ch);

                switch(pieceChar){
                    case 'r':
                        tiles.get(col).get(row).setPiece(new Castle(col, row, isWhite));
                        addToBoard(tiles.get(col).get(row).getPiece());
                        break;
                    case 'n':
                        tiles.get(col).get(row).setPiece(new Horse(col, row, isWhite));
                        addToBoard(tiles.get(col).get(row).getPiece());
                        break;
                    case 'b':
                        tiles.get(col).get(row).setPiece(new Bishop(col, row, isWhite));
                        addToBoard(tiles.get(col).get(row).getPiece());
                        break;
                    case 'q':
                        tiles.get(col).get(row).setPiece(new Queen(col, row, isWhite));
                        addToBoard(tiles.get(col).get(row).getPiece());
                        break;
                    case 'k':
                        tiles.get(col).get(row).setPiece(new King(col, row, isWhite));
                        addToBoard(tiles.get(col).get(row).getPiece());
                        break;
                    case 'p':
                        Piece pawn = new Pawn(col, row, isWhite);
                        if((pawn.getIsWhite() && pawn.getRow() != 6) || (!pawn.getIsWhite() && pawn.getRow() != 1)){
                            pawn.setIsFirstMove(false);
                        }
                        addToBoard(pawn);
                        tiles.get(col).get(row).setPiece(pawn);
                        break;
                }
                col++;
            }
        }
    }

}
