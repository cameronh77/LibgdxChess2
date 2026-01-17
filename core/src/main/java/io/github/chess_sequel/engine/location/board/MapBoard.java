package io.github.chess_sequel.engine.location.board;

import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.interactables.NPCPiece;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.player.Player;

import java.util.ArrayList;

public class MapBoard extends Board{


    private ArrayList<Interactable> locations = new ArrayList<>();

    public MapBoard(GameRun gameRun, int boardX, int boardY, Player player, String boardLayout, String internalLayouts){
        super(boardX, boardY, player, null);
        populateBoard(boardLayout, gameRun, internalLayouts);
        //addToBoard(player.getLeadPiece());

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

    public void populateBoard(String boardLayout, GameRun gameRun, String internalLayouts){
        pieces.clear();
        String[] parts = boardLayout.split(" ");
        String[] internals = internalLayouts.split(" ");

        for(int index = 0; index< parts.length;index++){
            System.out.println(parts[index]);
            switch(parts[index].charAt(0)){
                case ('e'):
                    BotPlayer botPlayer = new BotPlayer(gameRun, 3, gameRun.getJsonLoader().getEnemyData(gameRun.getCurrentMap(), internals[index]).enemyLayout, gameRun.getJsonLoader().getEnemyData(gameRun.getCurrentMap(), internals[index]).rewards);
                    addLocation(new NPCPiece(botPlayer, gameRun, Character.getNumericValue(parts[index].charAt(1)), Character.getNumericValue(parts[index].charAt(2))));
                    break;
            }
        }
        /**
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
         */
    }

    public ArrayList<Interactable> getLocations(){
        return locations;
    }

}
