package io.github.chess_sequel.engine.pieces.classic;


import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

/** Standard knight (named "horse") — jumps in an L-shape and can leap over other pieces. */
public class Horse extends Piece {

    public Horse(int x, int y, boolean isBlack){
        super(x, y, isBlack, "horse", ChessClass.CLASSIC);
        pieceType = PieceType.HORSE;
    }

    @Override
    public String getDescription() { return "Jumps in an L-shape: two squares one way, one to the side. Can leap over other pieces."; }

    public Horse(int x, int y, boolean isBlack, String name, ChessClass chessClass){
        super(x, y, isBlack, name, chessClass);
        pieceType = PieceType.HORSE;
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck){
        if(board instanceof AlterLayoutBoard){
            return generateAlterLayoutMoves(board);
        }

        ArrayList<Move> moves = new ArrayList<>();

        if(isBlack == board.getWhiteToMove()) {
            moves.add(new Move(this, col + 1, row + 2, board));
            moves.add(new Move(this, col + 1, row - 2, board));
            moves.add(new Move(this, col - 1, row + 2, board));
            moves.add(new Move(this, col - 1, row - 2, board));

            moves.add(new Move(this, col + 2, row + 1, board));
            moves.add(new Move(this, col + 2, row - 1, board));
            moves.add(new Move(this, col - 2, row + 1, board));
            moves.add(new Move(this, col - 2, row - 1, board));

            ArrayList<Move> trueMoves = new ArrayList<>();

            for (Move move : moves) {
                if (move.getNewX() >= 0 && move.getNewX() < board.boardX && move.getNewY() >= 0 && move.getNewY() < board.boardY) {
                    if (!(board.getTiles().get(move.getNewX()).get(move.getNewY()).getPiece() != null && board.getTiles().get(move.getNewX()).get(move.getNewY()).getPiece().getIsBlack() == isBlack)) {
                        trueMoves.add(move);
                    }
                }
            }

            if(!ignoreCheck){
                //Ignore the naming convention for now
                ArrayList<Move> truerMoves = new ArrayList<>();
                for (Move move : trueMoves) {
                    if (!board.checkEvaluator(move)) {
                        truerMoves.add(move);
                    }
                }
                return truerMoves;
            }


            return trueMoves;
        }
        else{
            return moves;
        }
    }

}
