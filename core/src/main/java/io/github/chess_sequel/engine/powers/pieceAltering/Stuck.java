package io.github.chess_sequel.engine.powers.pieceAltering;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;

import java.util.ArrayList;

public class Stuck extends AlterMovePower{

    public Stuck(int duration){
        super(null, duration);
    }

    @Override
    public ArrayList<Move> alterMoves(ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (duration > 0) {
            System.out.println("clearing moves");
            moves.clear();
        }

        return moves;

    }
}
