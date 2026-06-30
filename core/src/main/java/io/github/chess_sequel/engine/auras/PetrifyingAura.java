package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

/**
 * Tile-level aura placed by the {@link io.github.chess_sequel.engine.pieces.goblin.GoblinQueen}
 * on all 8 adjacent tiles. Clears the move list of any enemy non-King piece, preventing it
 * from moving through those tiles.
 */
public class PetrifyingAura extends Aura{

    public PetrifyingAura(Piece owner){
        super(owner, "petrifyingAura");
        this.imagePath = "tileModifiers/petrifying-aura.png";
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck){
        if(piece.isBlack() != owner.isBlack() && piece.getPieceType() != PieceType.KING){
            moves.clear();
        }

        return moves;
    }
}
