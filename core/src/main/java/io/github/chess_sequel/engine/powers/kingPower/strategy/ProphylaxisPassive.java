package io.github.chess_sequel.engine.powers.kingPower.strategy;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProphylaxisPassive extends PassiveKingPower {

    public ProphylaxisPassive(Piece king) {
        super(king, "Prophylaxis");
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (piece.isBlack() == owner.isBlack()) return moves;

        Map<String, Integer> coverage = buildCoverageMap(board);
        moves.removeIf(move -> pathBlocked(
            piece.getCol(), piece.getRow(), move.getNewX(), move.getNewY(), coverage));

        return moves;
    }

    private Map<String, Integer> buildCoverageMap(Board board) {
        Map<String, Integer> coverage = new HashMap<>();
        for (Piece p : board.getPieces()) {
            if (p.isBlack() != owner.isBlack()) continue;
            for (Move m : p.generateMoves(board, false)) {
                String key = m.getNewX() + "," + m.getNewY();
                coverage.merge(key, 1, Integer::sum);
            }
        }
        return coverage;
    }

    private boolean pathBlocked(int fromCol, int fromRow, int toCol, int toRow, Map<String, Integer> coverage) {
        int dCol = toCol - fromCol;
        int dRow = toRow - fromRow;

        boolean vertical   = dCol == 0;
        boolean horizontal = dRow == 0;
        boolean diagonal   = Math.abs(dCol) == Math.abs(dRow);

        // Knights and other jumping pieces have no intermediate squares
        if (!vertical && !horizontal && !diagonal) return false;
        // 1-square moves have no intermediate squares
        if (Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1) return false;

        int dc = Integer.signum(dCol);
        int dr = Integer.signum(dRow);

        int c = fromCol + dc;
        int r = fromRow + dr;
        while (c != toCol || r != toRow) {
            if (coverage.getOrDefault(c + "," + r, 0) >= 2) return true;
            c += dc;
            r += dr;
        }
        return false;
    }

    @Override public String getName() { return "Prophylaxis"; }
    @Override public String getIconPath() { return "kingPowers/prophylaxis.png"; }
    @Override public String getDescription() { return "Squares covered by 2 or more friendly pieces are locked — enemy sliding pieces cannot move through them, only to them."; }
}
