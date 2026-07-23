package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

/**
 * Queen move for the Berserker. After landing, all pieces in the 3x3 around the destination
 * are captured (including friendlies, excluding kings). The extra captures are tracked here
 * so they can be restored on undo.
 */
public class BerserkerMove extends Move {

    final ArrayList<Piece> aoeCaptures = new ArrayList<>();
    final ArrayList<int[]> aoePositions = new ArrayList<>();

    public BerserkerMove(Piece piece, int newX, int newY, Board board) {
        super(piece, newX, newY, board);
    }

    /** Called from Berserker.postMove — removes all non-king pieces in the 3x3 around the berserker. */
    public void captureAoe(Piece berserker, Board board) {
        int cx = berserker.getCol();
        int cy = berserker.getRow();
        for (int dc = -1; dc <= 1; dc++) {
            for (int dr = -1; dr <= 1; dr++) {
                int tc = cx + dc, tr = cy + dr;
                if (tc < 0 || tc >= board.boardX || tr < 0 || tr >= board.boardY) continue;
                Piece p = board.getTiles().get(tc).get(tr).getPiece();
                if (p == null || p == berserker) continue;
                if (p.getPieceType() == PieceType.KING) continue;
                aoeCaptures.add(p);
                aoePositions.add(new int[]{tc, tr});
                board.getTiles().get(tc).get(tr).setPiece(null);
                board.getPieces().remove(p);
                p.onCapture(berserker);
            }
        }
    }

    /** Called from Berserker.undoPostMove — restores all AOE-captured pieces. */
    public void restoreAoe(Board board, Piece berserker) {
        for (int i = aoeCaptures.size() - 1; i >= 0; i--) {
            Piece p = aoeCaptures.get(i);
            int[] pos = aoePositions.get(i);
            board.getTiles().get(pos[0]).get(pos[1]).setPiece(p);
            board.getPieces().add(p);
            p.undoOnCapture(berserker);
        }
        aoeCaptures.clear();
        aoePositions.clear();
    }
}
