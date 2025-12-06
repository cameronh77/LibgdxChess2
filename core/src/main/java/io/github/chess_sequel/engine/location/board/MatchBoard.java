package io.github.chess_sequel.engine.location.board;


import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.pieces.*;
import io.github.chess_sequel.engine.player.Player;

import java.util.ArrayList;

public class MatchBoard extends Board{





    public MatchBoard(int boardX, int boardY, Player opponent){
        super(boardX, boardY, opponent);
    }


    public void addToBoard(Piece piece){
        pieces.add(piece);
        tiles.get(piece.getCol()).get(piece.getRow()).setPiece(piece);
    }






    public void addKiwipetePieces() {
        addToBoard(new Castle(0, 0, false)); // a8: black rook
        addToBoard(new King(4, 0, false));   // e8: black king
        addToBoard(new Castle(7, 0, false)); // h8: black rook

        // Rank 7 (row 1)
        addToBoard(new Pawn(0, 1, false));   // a7
        addToBoard(new Pawn(2, 1, false));   // c7
        addToBoard(new Pawn(3, 1, false));   // d7
        addToBoard(new Queen(4, 1, false));  // e7
        addToBoard(new Pawn(5, 1, false));   // f7
        addToBoard(new Bishop(6, 1, false)); // g7

        // Rank 6 (row 2)
        addToBoard(new Bishop(0, 2, false)); // a6
        addToBoard(new Horse(1, 2, false));  // b6
        addToBoard(new Pawn(4, 2, false));   // e6
        addToBoard(new Horse(5, 2, false));  // f6
        addToBoard(new Pawn(6, 2, false));   // g6

        // Rank 5 (row 3)
        addToBoard(new Pawn(3, 3, true));    // d5 (white pawn)
        addToBoard(new Horse(4, 3, true));   // e5 (white knight)

        // Rank 4 (row 4)
        addToBoard(new Pawn(1, 4, false));   // b4 (black pawn)
        addToBoard(new Pawn(4, 4, true));    // e4 (white pawn)

        // Rank 3 (row 5)
        addToBoard(new Horse(2, 5, true));   // c3 (white knight)
        addToBoard(new Queen(5, 5, true));   // f3 (white queen)
        addToBoard(new Pawn(7, 5, false));   // h3 (black pawn)

        // Rank 2 (row 6)
        addToBoard(new Pawn(0, 6, true));    // a2
        addToBoard(new Pawn(1, 6, true));    // b2
        addToBoard(new Pawn(2, 6, true));    // c2
        addToBoard(new Bishop(3, 6, true));  // d2
        addToBoard(new Bishop(4, 6, true));  // e2
        addToBoard(new Pawn(5, 6, true));    // f2
        addToBoard(new Pawn(6, 6, true));    // g2
        addToBoard(new Pawn(7, 6, true));    // h2

        // Rank 1 (row 7)
        addToBoard(new Castle(0, 7, true));  // a1: white rook
        addToBoard(new King(4, 7, true));    // e1: white king
        addToBoard(new Castle(7, 7, true));  // h1: white rook
    }













    public void loadPositionFromFEN(String fenString){
        pieces.clear();
        String[] parts = fenString.split(" ");

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

        whiteToMove = parts[1].equals("w");


        Piece bqr = tiles.get(0).get(0).getPiece();
        if (bqr instanceof Castle){
            bqr.setIsFirstMove(parts[2].contains("q"));
        }

        Piece bkr = tiles.get(7).get(0).getPiece();
        if (bkr instanceof Castle){
            bkr.setIsFirstMove(parts[2].contains("k"));
        }

        Piece wqr = tiles.get(0).get(7).getPiece();
        if (wqr instanceof Castle){
            wqr.setIsFirstMove(parts[2].contains("Q"));
        }

        Piece wkr = tiles.get(7).get(7).getPiece();
        if (wkr instanceof Castle){
            wkr.setIsFirstMove(parts[2].contains("K"));
        }


        if (parts[3].equals("-")) {
            enPassantTile = null;
        } else {
            int file = parts[3].charAt(0) - 'a';           // column 0–7
            int rank = 7 - (parts[3].charAt(1) - '1');     // row 0–7 (top-down)
            int[] enPassantArray = {rank, file};
            enPassantTile = enPassantArray;
        }
    }



}
