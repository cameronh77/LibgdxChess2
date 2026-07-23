package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.kingMoves.ZugzwangMove;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;

import java.util.ArrayList;

public class ZugzwangActive extends ActiveKingPower {

    private final Piece king;

    public ZugzwangActive(Piece king) {
        this.king = king;
        this.charges = 1;
    }

    @Override
    public ArrayList<Move> generateMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        if (!isAvailable()) return moves;

        for (Piece p : board.getPieces()) {
            if (p.isBlack() == king.isBlack()) continue;
            moves.add(new ZugzwangMove(king, p, board, this));
        }

        return moves;
    }

    @Override public String getName() { return "Zugzwang"; }
    @Override public String getIconPath() { return "kingPowers/zugzwang.png"; }
    @Override public String getDescription() { return "Once per game, force an enemy piece to move next turn — all other enemy pieces are frozen. If the chosen piece has no legal moves, the opponent loses their turn entirely."; }
}
