package io.github.chess_sequel.engine.powers.kingPower.goblin;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.kingMoves.GoblinSwapMove;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

/**
 * Goblin active power: once per game, after losing at least 50% of your starting pieces,
 * swap the king's position with any allied piece on the board.
 */
public class GoblinSwapActive extends ActiveKingPower {

    private final Piece king;
    private int startingCount = -1;
    private Board lastBoard = null;

    public GoblinSwapActive(Piece king) {
        this.king = king;
        this.charges = 1;
    }

    @Override
    public String getName() { return "Goblin's Gambit"; }

    @Override
    public String getIconPath() { return "kingPowers/goblin-surge.png"; }

    @Override
    public String getDescription() { return "Once per game, when you've lost 50%+ of your pieces, swap your King with any piece on the board."; }

    @Override
    public boolean isAvailable() { return charges > 0; }

    @Override
    public boolean isAvailable(Board board) {
        if (board != lastBoard) {
            startingCount = -1;
            lastBoard = board;
        }
        if (startingCount == -1) {
            startingCount = friendlyCount(board);
        }
        return charges > 0 && startingCount > 1 && friendlyCount(board) * 2 <= startingCount;
    }

    private int friendlyCount(Board board) {
        int count = 0;
        for (Piece p : board.getPieces()) {
            if (p.getIsBlack() == king.getIsBlack()) count++;
        }
        return count;
    }

    @Override
    public ArrayList<Move> generateMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        if (!isAvailable(board)) return moves;
        for (Piece p : board.getPieces()) {
            if (p == king) continue;
            moves.add(new GoblinSwapMove(king, p, board, this));
        }
        return moves;
    }
}
