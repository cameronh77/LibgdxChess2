package io.github.chess_sequel.engine.perftChecker;

import io.github.chess_sequel.engine.location.Board;

import io.github.chess_sequel.engine.pieces.Piece;


public class BoardEvaluator {

    private static final int kingVal = 10000;
    private static final int bishopVal = 50;
    private static final int queenVal = 100;
    private static final int horseVal = 75;
    private static final int castleVal = 80;
    private static final int pawnVal = 10;


    public static int evaluatePosition(Board board, Boolean whiteToMove){
        int posVal = 0;
        for(Piece piece: board.getPieces()){
            switch(piece.getPieceType()) {
                case KING:
                    posVal += whiteToMove ? kingVal : -kingVal;
                    break;
                case QUEEN:
                    posVal += whiteToMove ? queenVal : -queenVal;
                    break;
                case BISHOP:
                    posVal += whiteToMove ? bishopVal: - bishopVal;
                    break;
                case HORSE:
                    posVal += whiteToMove ? horseVal: - horseVal;
                    break;
                case CASTLE:
                    posVal += whiteToMove ? castleVal: - castleVal;
                    break;
                case PAWN:
                    posVal += whiteToMove ? pawnVal: - pawnVal;
                    break;
            }
        }

        return posVal;
    }
}
