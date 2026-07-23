package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.PersistentTrapMove;
import io.github.chess_sequel.engine.moves.TrapMove;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.war.strategy.TrapPawn;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;

import java.util.ArrayList;

/**
 * Passive strategy king power: TrapPawns can place traps up to 2 squares diagonally
 * forward, provided both the intermediate and destination squares are unoccupied.
 * If Persistence is also active, the extended-range traps also keep the previous trap.
 */
public class OverwatchPassive extends PassiveKingPower {

    public OverwatchPassive(Piece king) {
        super(king, "Overwatch");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (!(piece instanceof TrapPawn) || piece.isBlack() != owner.isBlack()) return moves;
        // Only add moves on the TrapPawn's turn (mirrors TrapPawn.generateBaseMoves guard)
        if (piece.isBlack() != board.getWhiteToMove()) return moves;

        boolean persistent = hasPersistence(board);

        int col = piece.getCol();
        int row = piece.getRow();
        int offset = piece.isBlack() ? -1 : 1;

        for (int dc : new int[]{-2, 2}) {
            int intCol = col + dc / 2;
            int intRow = row + offset;
            int destCol = col + dc;
            int destRow = row + 2 * offset;

            if (intCol < 0 || intRow < 0 || intCol >= board.boardX || intRow >= board.boardY) continue;
            if (destCol < 0 || destRow < 0 || destCol >= board.boardX || destRow >= board.boardY) continue;
            if (board.getTiles().get(intCol).get(intRow).getPiece() != null) continue;
            if (board.getTiles().get(destCol).get(destRow).getPiece() != null) continue;

            moves.add(persistent
                ? new PersistentTrapMove((TrapPawn) piece, destCol, destRow, board)
                : new TrapMove((TrapPawn) piece, destCol, destRow, board));
        }

        return moves;
    }

    private boolean hasPersistence(Board board) {
        for (Aura aura : board.getBoardAuras()) {
            if (aura instanceof PersistencePassive && aura.getOwner().isBlack() == owner.isBlack()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() { return "Overwatch"; }

    @Override
    public String getIconPath() { return "kingPowers/overwatch.png"; }

    @Override
    public String getDescription() { return "TrapPawns can place traps 2 squares diagonally forward if both squares are clear."; }
}
