package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.kingMoves.KingSnareMove;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;

import java.util.ArrayList;

/**
 * Strategy active power: once per game, place a trap on any empty tile.
 * The trap is owned by the king — when an enemy steps on it, the king teleports
 * there and captures them, but is now potentially exposed in enemy territory.
 */
public class KingSnareActive extends ActiveKingPower {

    private final Piece king;

    public KingSnareActive(Piece king) {
        this.king = king;
        this.charges = 1;
    }

    @Override
    public ArrayList<Move> generateMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        if (!isAvailable()) return moves;

        for (int c = 0; c < board.boardX; c++) {
            for (int r = 0; r < board.boardY; r++) {
                if (board.getTiles().get(c).get(r).getPiece() != null) continue;
                moves.add(new KingSnareMove(king, c, r, board, this));
            }
        }

        return moves;
    }

    @Override
    public String getName() { return "King's Snare"; }

    @Override
    public String getIconPath() { return "kingPowers/kings-snare.png"; }

    @Override
    public String getDescription() { return "Once per game, place a trap on any empty tile. When triggered, the king teleports there — a powerful capture that may leave the king dangerously exposed."; }
}
