package io.github.chess_sequel.engine.powers.pieceAltering;

import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

public class BouncingBishops extends AlterMovePower{

    public BouncingBishops(){
        inheritors = new ArrayList<>();
        inheritors.add(PieceType.BISHOP);
    }

    @Override
    public ArrayList<Move> alterMoves(){
        return null;
    }
}
