package io.github.chess_sequel.engine.pieces.war.strategy;

import io.github.chess_sequel.engine.auras.TrapAura;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.TrapMove;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.Pawn;

import java.util.ArrayList;

/**
 * War piece — strategy path.
 *
 * Moves like a normal pawn. On empty forward diagonals it can also place a trap:
 * any enemy that steps onto the trap is automatically captured and the pawn teleports there.
 * Placing a new trap removes the old one. Occupied diagonals are handled as normal captures.
 */
public class TrapPawn extends Pawn {

    TrapAura currentTrap;

    public TrapPawn(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "trap-pawn", ChessClass.STRATEGY);
    }

    @Override
    public String getDescription() {
        return "Moves like a normal pawn. Places a trap on empty forward diagonals — any enemy that steps there is captured and the pawn teleports to that tile.";
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) {
            return generateAlterLayoutMoves(board);
        }

        // Normal pawn moves (forward march, diagonal captures, en passant, promotion)
        ArrayList<Move> moves = super.generateBaseMoves(board, true);

        if (isBlack == board.getWhiteToMove()) {
            int offset = isBlack ? -1 : 1;
            int nextRow = row + offset;
            if (nextRow >= 0 && nextRow < board.boardY) {
                // Trap on empty forward diagonals only
                if (col - 1 >= 0 && board.getTiles().get(col - 1).get(nextRow).getPiece() == null) {
                    moves.add(new TrapMove(this, col - 1, nextRow, board));
                }
                if (col + 1 < board.boardX && board.getTiles().get(col + 1).get(nextRow).getPiece() == null) {
                    moves.add(new TrapMove(this, col + 1, nextRow, board));
                }
            }
        }

        if (!ignoreCheck) {
            ArrayList<Move> safe = new ArrayList<>();
            for (Move m : moves) {
                if (!board.checkEvaluator(m)) safe.add(m);
            }
            return safe;
        }

        return moves;
    }

    @Override
    public void postMove(Move move, Board board) {
        if (move.getMovingPiece() != this) return;
        if (currentTrap == null) return;
        board.removeAura(currentTrap);
    }

    @Override
    public void undoPostMove(Move move, Board board) {
        if (move.getMovingPiece() != this) return;
        if (currentTrap == null) return;
        board.addAura(currentTrap);
    }

    public TrapAura getCurrentTrap() { return currentTrap; }
    public void setCurrentTrap(TrapAura trap) { this.currentTrap = trap; }
}
