package io.github.chess_sequel.engine.powers.kingPower.classic;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Queen;

import java.util.ArrayList;

/**
 * Passive king power: "Queenly Majesty". Friendly pieces that are adjacent to a friendly
 * queen inherit queen-like movement (sliding in all 8 directions) for that turn.
 * Queens themselves are unaffected (they already have queen moves).
 */
public class QueenlyMajestyPassive extends PassiveKingPower {

    public QueenlyMajestyPassive(Piece king) {
        super(king, "Queenly Majesty");
    }

    @Override
    public String getIconPath() { return "kingPowers/queenly-majesty.png"; }

    @Override
    public String getDescription() { return "Pieces adjacent to your Queen can move like a Queen."; }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean ignoreCheck) {
        if (piece instanceof Queen) return moves;
        if (piece.getIsBlack() != owner.getIsBlack()) return moves;
        if (piece.getIsBlack() != board.getWhiteToMove()) return moves;

        boolean adjacentToQueen = false;
        for (Piece p : board.getPieces()) {
            if (!(p instanceof Queen)) continue;
            if (p.getIsBlack() != owner.getIsBlack()) continue;
            if (Math.abs(p.getCol() - piece.getCol()) <= 1 && Math.abs(p.getRow() - piece.getRow()) <= 1) {
                adjacentToQueen = true;
                break;
            }
        }
        if (!adjacentToQueen) return moves;

        ArrayList<Move> queenMoves = generateQueenMoves(piece, board);

        if (!ignoreCheck) {
            ArrayList<Move> safe = new ArrayList<>();
            for (Move m : queenMoves) {
                if (!board.checkEvaluator(m)) safe.add(m);
            }
            queenMoves = safe;
        }

        for (Move qm : queenMoves) {
            boolean dup = false;
            for (Move m : moves) {
                if (m.getNewX() == qm.getNewX() && m.getNewY() == qm.getNewY()) { dup = true; break; }
            }
            if (!dup) moves.add(qm);
        }

        return moves;
    }

    private ArrayList<Move> generateQueenMoves(Piece piece, Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        int col = piece.getCol();
        int row = piece.getRow();
        boolean isBlack = piece.getIsBlack();

        int[][] dirs = {{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] dir : dirs) {
            int c = col + dir[0], r = row + dir[1];
            while (c >= 0 && c < board.boardX && r >= 0 && r < board.boardY) {
                Piece target = board.getTiles().get(c).get(r).getPiece();
                if (target != null) {
                    if (target.getIsBlack() != isBlack) moves.add(new Move(piece, c, r, board));
                    break;
                }
                moves.add(new Move(piece, c, r, board));
                c += dir[0];
                r += dir[1];
            }
        }
        return moves;
    }
}
