package io.github.chess_sequel.engine.pieces;


import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.EnPassant;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.Promotion;

import java.util.ArrayList;

public class Pawn extends Piece{

    public Pawn(int x, int y, boolean isWhite){
        super(x, y, isWhite, "pawn");
        pieceType = PieceType.PAWN;

    }

    @Override
    public ArrayList<Move> generateMoves(Board board, Boolean ignoreCheck){
        ArrayList<Move> moves = new ArrayList<>();
        if(isWhite == board.getWhiteToMove()) {
            int offset = isWhite ? -1 : 1; //Set offset
            //Generate moves for first turn not considering promotion on first turn, can revisit
            if (this.isFirstMove) {
                if (board.getTiles().get(col).get(row + (2 * offset)).getPiece() == null && board.getTiles().get(col).get(row + (offset)).getPiece() == null) {
                    moves.add(new Move(this, col, row + (2 * offset), board));
                }
            }
            //Generate single moves
            System.out.println("I'm a pawn");
            System.out.println(col);
            System.out.println(row + offset);
            if ((row+offset) > 0 && row + offset < board.boardY && board.getTiles().get(col).get(row + (offset)).getPiece() == null) {

                if (row + (offset) == (isWhite ? 0 : board.boardX - 1)) {
                    moves.add(new Promotion(this, col, row + (offset), board, PieceType.QUEEN));
                    moves.add(new Promotion(this, col, row + (offset), board, PieceType.BISHOP));
                    moves.add(new Promotion(this, col, row + (offset), board, PieceType.CASTLE));
                    moves.add(new Promotion(this, col, row + (offset), board, PieceType.HORSE));
                }else {
                    moves.add(new Move(this, col, row + (offset), board));
                }
            }

            //Generate left takes (This could probably be more efficient)
            if ((row+offset) > 0 && row + offset < board.boardY && col - 1 >= 0 && board.getTiles().get(col - 1).get(row + (offset)).getPiece() != null && board.getTiles().get(col - 1).get(row + (offset)).getPiece().getIsWhite() != isWhite) {
                if (row + (offset) == (isWhite ? 0 : board.boardX - 1)) {
                    moves.add(new Promotion(this, col-1, row + (offset), board, PieceType.QUEEN));
                    moves.add(new Promotion(this, col-1, row + (offset), board, PieceType.BISHOP));
                    moves.add(new Promotion(this, col-1, row + (offset), board, PieceType.CASTLE));
                    moves.add(new Promotion(this, col-1, row + (offset), board, PieceType.HORSE));
                } else {
                    moves.add(new Move(this, col - 1, row + (offset), board));
                }
            }

            //Generate right takes
            if ((row+offset) > 0 && row + offset < board.boardY && col + 1 < board.boardX && board.getTiles().get(col + 1).get(row + (offset)).getPiece() != null && board.getTiles().get(col + 1).get(row + (offset)).getPiece().getIsWhite() != isWhite) {
                if (row + (offset) == (isWhite ? 0 : board.boardX - 1)) {
                    moves.add(new Promotion(this, col+1, row + (offset), board, PieceType.QUEEN));
                    moves.add(new Promotion(this, col+1, row + (offset), board, PieceType.BISHOP));
                    moves.add(new Promotion(this, col+1, row + (offset), board, PieceType.CASTLE));
                    moves.add(new Promotion(this, col+1, row + (offset), board, PieceType.HORSE));
                } else {
                    moves.add(new Move(this, col + 1, row + (offset), board));
                }
            }

            if (board.getEnPassantTile() != null) {
                //Generate left en passant
                if (col - 1 == board.getEnPassantTile()[0] && row + (offset) == board.getEnPassantTile()[1]) {
                    moves.add(new EnPassant(this, col - 1, row + (offset), board));
                }

                //Generate left en passant
                if (col + 1 == board.getEnPassantTile()[0] && row + (offset) == board.getEnPassantTile()[1]) {
                    moves.add(new EnPassant(this, col + 1, row + (offset), board));
                }
            }
            //System.out.println(moves);
            if(!ignoreCheck){
                //Ignore the naming convention for now
                ArrayList<Move> trueMoves = new ArrayList<>();
                for (Move move : moves) {
                    if (!board.checkEvaluator(move)) {
                        trueMoves.add(move);
                    }
                }
                return trueMoves;
            }
            return moves;
        } else {
            return moves;
        }
    }

}
