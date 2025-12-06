package io.github.chess_sequel.engine.pieces;


import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.moves.Move;

import java.util.ArrayList;

public class Horse extends Piece {

    public Horse(int x, int y, boolean isWhite){
        super(x, y, isWhite, "horse");
        pieceType = PieceType.HORSE;

    }

    @Override
    public ArrayList<Move> generateMoves(Board board, Boolean ignoreCheck){

        ArrayList<Move> moves = new ArrayList<>();

        if(isWhite == board.getWhiteToMove()) {
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
                    if (!(board.getTiles().get(move.getNewX()).get(move.getNewY()).getPiece() != null && board.getTiles().get(move.getNewX()).get(move.getNewY()).getPiece().getIsWhite() == isWhite)) {
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
