package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

public class PetrifyingAura extends Aura{



    public PetrifyingAura(Piece owner){
        super(owner, "petrifyingAura");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck){
        if(piece.isWhite() != owner.isWhite() && piece.getPieceType() != PieceType.KING){
            moves.clear();
        }

        return moves;
    }
}
