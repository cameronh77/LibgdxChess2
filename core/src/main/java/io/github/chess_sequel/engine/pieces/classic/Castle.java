package io.github.chess_sequel.engine.pieces.classic;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

public class Castle extends Piece {

    public Castle(int x, int y, boolean isWhite){
        super(x, y, isWhite, "castle", ChessClass.CLASSIC);
        pieceType = PieceType.CASTLE;
    }

    public Castle(int x, int y, boolean isWhite, String name, ChessClass chessClass){
        super(x, y, isWhite, name, chessClass);
        pieceType = PieceType.CASTLE;
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck){
        if(board instanceof AlterLayoutBoard){
            return generateAlterLayoutMoves(board);
        }
        ArrayList<Move> moves = new ArrayList<>();
        if(isWhite == board.getWhiteToMove()) {
            //pos x
            for (int c = col + 1; c < board.boardX; c += 1) {
                if (board.getTiles().get(c).get(row).getPiece() != null) {
                    if (board.getTiles().get(c).get(row).getPiece().getIsWhite() != isWhite) {
                        moves.add(new Move(this, c, row, board));
                    }
                    break;
                } else {
                    moves.add(new Move(this, c, row, board));
                }
            }

            //pos y
            for (int r = row + 1; r < board.boardY; r += 1) {
                if (board.getTiles().get(col).get(r).getPiece() != null) {
                    if (board.getTiles().get(col).get(r).getPiece().getIsWhite() != isWhite) {
                        moves.add(new Move(this, col, r, board));
                    }
                    break;
                } else {
                    moves.add(new Move(this, col, r, board));
                }
            }

            //neg x
            for (int c = col - 1; c >= 0; c -= 1) {
                if (board.getTiles().get(c).get(row).getPiece() != null) {
                    if (board.getTiles().get(c).get(row).getPiece().getIsWhite() != isWhite) {
                        moves.add(new Move(this, c, row, board));
                    }
                    break;
                } else {
                    moves.add(new Move(this, c, row, board));
                }
            }

            //neg y
            for (int r = row - 1; r >= 0; r -= 1) {
                if (board.getTiles().get(col).get(r).getPiece() != null) {
                    if (board.getTiles().get(col).get(r).getPiece().getIsWhite() != isWhite) {
                        moves.add(new Move(this, col, r, board));
                    }
                    break;
                } else {
                    moves.add(new Move(this, col, r, board));
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
        }
        return moves;
    }
}
