package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.CommanderPawnMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.TrapMove;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;
import io.github.chess_sequel.engine.pieces.war.strategy.Commander;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;

import java.util.ArrayList;

/**
 * Passive strategy king power: the Commander's free-move aura extends to all adjacent
 * friendly pieces, not just pawns. Kings and other Commanders are excluded.
 */
public class FullCommandPassive extends PassiveKingPower {

    private static final int[] DC = {-1, 0, 1, -1, 1, -1, 0, 1};
    private static final int[] DR = {-1, -1, -1,  0, 0,  1, 1, 1};

    public FullCommandPassive(Piece king) {
        super(king, "FullCommand");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (!(piece instanceof Commander) || piece.isBlack() != owner.isBlack()) return moves;
        // Same turn guard as Commander.generateBaseMoves
        if (piece.isBlack() != board.getWhiteToMove()) return moves;

        for (int i = 0; i < 8; i++) {
            int nc = piece.getCol() + DC[i];
            int nr = piece.getRow() + DR[i];
            if (nc < 0 || nc >= board.boardX || nr < 0 || nr >= board.boardY) continue;

            Piece adj = board.getTiles().get(nc).get(nr).getPiece();
            if (adj == null || adj.isBlack() != piece.isBlack()) continue;
            if (adj.getPieceType() == PieceType.PAWN) continue;  // already handled by Commander
            if (adj.getPieceType() == PieceType.KING) continue;  // king stays independent
            if (adj instanceof Commander) continue;               // no commander chains

            for (Move adjMove : adj.generateMoves(board, isCheck)) {
                if (adjMove instanceof TrapMove) continue;
                moves.add(new CommanderPawnMove(adj, adjMove.getNewX(), adjMove.getNewY(), board));
            }
        }

        return moves;
    }

    @Override
    public String getName() { return "Full Command"; }

    @Override
    public String getIconPath() { return "kingPowers/full-command.png"; }

    @Override
    public String getDescription() { return "The Commander's free-move aura extends to all adjacent friendly pieces, not just pawns."; }
}
