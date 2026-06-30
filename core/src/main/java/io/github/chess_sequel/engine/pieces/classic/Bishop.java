package io.github.chess_sequel.engine.pieces.classic;


import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(int x, int y, boolean isBlack){
        super(x, y, isBlack, "bishop", ChessClass.CLASSIC);
        pieceType = PieceType.BISHOP;
    }

    public Bishop(int x, int y, boolean isBlack, String name, ChessClass chessClass){
        super(x, y, isBlack, name, chessClass);
        pieceType = PieceType.BISHOP;
    }

    @Override
    public String getDescription() { return "Moves any number of squares diagonally. Always stays on the same colour."; }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck){
        if(board instanceof AlterLayoutBoard){
            return generateAlterLayoutMoves(board);
        }
        ArrayList<Move> moves = new ArrayList<>();
        if(isBlack == board.getWhiteToMove()) {
            //pos pos
            for (int offset = 1; offset + col < board.boardX && offset + row < board.boardY; offset += 1) {
                if (board.getTiles().get(col + offset).get(row + offset).getPiece() != null) {
                    if (board.getTiles().get(col + offset).get(row + offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new Move(this, col + offset, row + offset, board));
                    }
                    break;
                } else {
                    moves.add(new Move(this, col + offset, row + offset, board));
                }
            }

            //pos neg
            for (int offset = 1; offset + col < board.boardX && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col + offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col + offset).get(row - offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new Move(this, col + offset, row - offset, board));
                    }
                    break;
                } else {
                    moves.add(new Move(this, col + offset, row - offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && offset + row < board.boardY; offset += 1) {
                if (board.getTiles().get(col - offset).get(row + offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row + offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new Move(this, col - offset, row + offset, board));
                    }
                    break;
                } else {
                    moves.add(new Move(this, col - offset, row + offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col - offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row - offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new Move(this, col - offset, row - offset, board));
                    }
                    break;
                } else {
                    moves.add(new Move(this, col - offset, row - offset, board));
                }
            }

            if(!ignoreCheck){
                ArrayList<Move> trueMoves = new ArrayList<>();
                for (Move move : moves) {
                    if (!board.checkEvaluator(move)) {
                        trueMoves.add(move);
                    }
                }
                return trueMoves;
            }

            return moves;
        } else{
            return moves;
        }

    }

}
