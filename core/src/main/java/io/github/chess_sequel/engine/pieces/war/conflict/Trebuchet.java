package io.github.chess_sequel.engine.pieces.war.conflict;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.TrebuchetFireMove;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Castle;

import java.util.ArrayList;

/**
 * Conflict castle. Moves only one tile in any orthogonal direction (unlike a standard rook's
 * unlimited slide). In addition, generates a fire move for every enemy piece visible along
 * its row or column: firing removes the target without the Trebuchet moving.
 */
public class Trebuchet extends Castle {

    public Trebuchet(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "trebuchet", ChessClass.CONFLICT);
    }

    @Override
    public String getDescription() {
        return "Moves one tile orthogonally. Can fire at any enemy in its row or column without moving.";
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        if (isBlack != board.getWhiteToMove()) return new ArrayList<>();

        ArrayList<Move> moves = new ArrayList<>();

        // Single-tile orthogonal movement
        int[][] deltas = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : deltas) {
            int tc = col + d[0], tr = row + d[1];
            if (tc < 0 || tc >= board.boardX || tr < 0 || tr >= board.boardY) continue;
            Piece occupant = board.getTiles().get(tc).get(tr).getPiece();
            if (occupant == null) {
                moves.add(new Move(this, tc, tr, board));
            } else if (occupant.getIsBlack() != isBlack) {
                moves.add(new Move(this, tc, tr, board));
            }
        }

        // Fire moves: first enemy in each orthogonal ray (blocked by any intervening piece)
        moves.addAll(fireMoves(board));

        if (!ignoreCheck) {
            ArrayList<Move> trueMoves = new ArrayList<>();
            for (Move move : moves) {
                if (!board.checkEvaluator(move)) trueMoves.add(move);
            }
            return trueMoves;
        }
        return moves;
    }

    private ArrayList<Move> fireMoves(Board board) {
        ArrayList<Move> fires = new ArrayList<>();
        fires.addAll(fireRay(board, 1, 0));
        fires.addAll(fireRay(board, -1, 0));
        fires.addAll(fireRay(board, 0, 1));
        fires.addAll(fireRay(board, 0, -1));
        return fires;
    }

    private ArrayList<Move> fireRay(Board board, int dc, int dr) {
        ArrayList<Move> fires = new ArrayList<>();
        for (int offset = 2; ; offset++) {  // start at 2: tile at offset 1 is the step-move, not a fire target
            int tc = col + dc * offset;
            int tr = row + dr * offset;
            if (tc < 0 || tc >= board.boardX || tr < 0 || tr >= board.boardY) break;
            Piece p = board.getTiles().get(tc).get(tr).getPiece();
            if (p != null) {
                if (p.getIsBlack() != isBlack) {
                    fires.add(new TrebuchetFireMove(this, p, tc, tr, board));
                }
                break; // blocked by any piece, friendly or not
            }
        }
        return fires;
    }
}
