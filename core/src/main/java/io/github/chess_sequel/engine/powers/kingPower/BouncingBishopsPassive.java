package io.github.chess_sequel.engine.powers.kingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Bishop;

import java.util.ArrayList;

public class BouncingBishopsPassive extends PassiveKingPower {

    public BouncingBishopsPassive(Piece king) {
        super(king, "Bouncing Bishops");
    }

    @Override
    public String getIconPath() { return "kingPowers/bouncing-bishops.png"; }

    @Override
    public String getDescription() { return "Your Bishops reflect off board edges, continuing their diagonal after hitting a wall."; }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean ignoreCheck) {
        if (!(piece instanceof Bishop)) return moves;
        if (piece.getIsBlack() != owner.getIsBlack()) return moves;
        if (piece.getIsBlack() != board.getWhiteToMove()) return moves;

        ArrayList<Move> bounced = new ArrayList<>();
        int[][] dirs = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] dir : dirs) {
            addBouncedMoves(piece, dir[0], dir[1], board, bounced);
        }

        if (!ignoreCheck) {
            ArrayList<Move> safe = new ArrayList<>();
            for (Move m : bounced) {
                if (!board.checkEvaluator(m)) safe.add(m);
            }
            bounced = safe;
        }

        for (Move bm : bounced) {
            boolean dup = false;
            for (Move m : moves) {
                if (m.getNewX() == bm.getNewX() && m.getNewY() == bm.getNewY()) { dup = true; break; }
            }
            if (!dup) moves.add(bm);
        }

        return moves;
    }

    private void addBouncedMoves(Piece bishop, int dx, int dy, Board board, ArrayList<Move> bounced) {
        int col = bishop.getCol() + dx;
        int row = bishop.getRow() + dy;

        // Phase 1: advance to the wall — these squares are already in the regular move list
        while (col >= 0 && col < board.boardX && row >= 0 && row < board.boardY) {
            if (board.getTiles().get(col).get(row).getPiece() != null) {
                return; // blocked before reaching the wall — no bounce
            }
            col += dx;
            row += dy;
        }

        // Reflect whichever axis left the board
        if (col < 0 || col >= board.boardX) dx = -dx;
        if (row < 0 || row >= board.boardY) dy = -dy;

        if (col < 0)              col = -col;
        else if (col >= board.boardX) col = 2 * (board.boardX - 1) - col;
        if (row < 0)              row = -row;
        else if (row >= board.boardY) row = 2 * (board.boardY - 1) - row;

        // Phase 2: trace the post-bounce ray
        while (col >= 0 && col < board.boardX && row >= 0 && row < board.boardY) {
            Piece tile = board.getTiles().get(col).get(row).getPiece();
            if (tile != null) {
                if (tile.getIsBlack() != bishop.getIsBlack()) {
                    bounced.add(new Move(bishop, col, row, board));
                }
                return;
            }
            bounced.add(new Move(bishop, col, row, board));
            col += dx;
            row += dy;
        }
    }
}
