package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.Iterator;

public class TollGateAura extends Aura {

    public TollGateAura(Piece owner) {
        super(owner, "tollGateAura");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (piece.isBlack() == owner.isBlack()) return moves;

        int tollRow = owner.getRow();
        int pieceRow = piece.getRow();

        if (pieceRow == tollRow) return moves;

        Iterator<Move> it = moves.iterator();
        if (pieceRow < tollRow) {
            while (it.hasNext()) {
                if (it.next().getNewY() > tollRow) it.remove();
            }
        } else {
            while (it.hasNext()) {
                if (it.next().getNewY() < tollRow) it.remove();
            }
        }

        return moves;
    }
}
