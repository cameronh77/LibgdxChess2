package io.github.chess_sequel.engine.pieces.classic;


import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.EnPassant;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.Promotion;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

/**
 * Standard pawn. Moves forward one square, two on its first move, and captures diagonally.
 * Supports en-passant and auto-promotes to Queen, Bishop, Rook, or Knight on reaching the
 * far rank.
 */
public class Pawn extends Piece {

    public Pawn(int x, int y, boolean isBlack){
        super(x, y, isBlack, "pawn", ChessClass.CLASSIC);
        pieceType = PieceType.PAWN;
    }

    public Pawn(int x, int y, boolean isBlack, String name, ChessClass chessClass){
        super(x, y, isBlack, name, chessClass);
        pieceType = PieceType.PAWN;
    }

    @Override
    public String getDescription() { return "Moves forward one square, captures diagonally. Can move two squares on its first move."; }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck){
        if(board instanceof AlterLayoutBoard){
            return generateAlterLayoutMoves(board);
        }
        ArrayList<Move> moves = new ArrayList<>();
        if(isBlack == board.getWhiteToMove()) {
            int offset = isBlack ? -1 : 1; //Set offset
            //Generate moves for first turn not considering promotion on first turn, can revisit
            if (this.isFirstMove && row + 2*offset > 0 && row+2*offset < board.boardX) {
                if (board.getTiles().get(col).get(row + (2 * offset)).getPiece() == null && board.getTiles().get(col).get(row + (offset)).getPiece() == null) {
                    moves.add(new Move(this, col, row + (2 * offset), board));
                }
            }
            //Generate single moves
            if ((row+offset) > 0 && row + offset < board.boardY && board.getTiles().get(col).get(row + (offset)).getPiece() == null) {

                if (row + (offset) == (isBlack ? 0 : board.boardX - 1)) {
                    moves.add(new Promotion(this, col, row + (offset), board, PieceType.QUEEN));
                    moves.add(new Promotion(this, col, row + (offset), board, PieceType.BISHOP));
                    moves.add(new Promotion(this, col, row + (offset), board, PieceType.CASTLE));
                    moves.add(new Promotion(this, col, row + (offset), board, PieceType.HORSE));
                }else {
                    moves.add(new Move(this, col, row + (offset), board));
                }
            }

            //Generate left takes (This could probably be more efficient)
            if ((row+offset) > 0 && row + offset < board.boardY && col - 1 >= 0 && board.getTiles().get(col - 1).get(row + (offset)).getPiece() != null && board.getTiles().get(col - 1).get(row + (offset)).getPiece().getIsBlack() != isBlack) {
                if (row + (offset) == (isBlack ? 0 : board.boardX - 1)) {
                    moves.add(new Promotion(this, col-1, row + (offset), board, PieceType.QUEEN));
                    moves.add(new Promotion(this, col-1, row + (offset), board, PieceType.BISHOP));
                    moves.add(new Promotion(this, col-1, row + (offset), board, PieceType.CASTLE));
                    moves.add(new Promotion(this, col-1, row + (offset), board, PieceType.HORSE));
                } else {
                    moves.add(new Move(this, col - 1, row + (offset), board));
                }
            }

            //Generate right takes
            if ((row+offset) > 0 && row + offset < board.boardY && col + 1 < board.boardX && board.getTiles().get(col + 1).get(row + (offset)).getPiece() != null && board.getTiles().get(col + 1).get(row + (offset)).getPiece().getIsBlack() != isBlack) {
                if (row + (offset) == (isBlack ? 0 : board.boardX - 1)) {
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
