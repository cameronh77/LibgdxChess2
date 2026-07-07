package io.github.chess_sequel.engine.powers.pieceAltering;

import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.classic.King;

import java.util.ArrayList;

/**
 * Single-use AlterMovePower that replaces the king's map moves with an alternate piece's
 * movement pattern for exactly one step. Duration 1 means it expires after the next move tick.
 */
public class MapEssenceAlterMove extends AlterMovePower {

    private final King king;
    private final String pieceType;

    public MapEssenceAlterMove(King king, String pieceType) {
        super(null, 1);
        this.king = king;
        this.pieceType = pieceType;
    }

    public String getPieceType() { return pieceType; }

    @Override
    public ArrayList<Move> alterMoves(ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (duration <= 0 || !(board instanceof MapBoard)) return moves;
        moves.clear();
        moves.addAll(generateEssenceMoves(board));
        return moves;
    }

    private ArrayList<Move> generateEssenceMoves(Board board) {
        switch (pieceType) {
            case "rook":   return generateRayMoves(board, new int[][]{{1,0},{-1,0},{0,1},{0,-1}});
            case "bishop": return generateRayMoves(board, new int[][]{{1,1},{-1,1},{1,-1},{-1,-1}});
            case "queen": {
                ArrayList<Move> m = generateRayMoves(board, new int[][]{{1,0},{-1,0},{0,1},{0,-1}});
                m.addAll(generateRayMoves(board, new int[][]{{1,1},{-1,1},{1,-1},{-1,-1}}));
                return m;
            }
            case "horse": return generateJumpMoves(board, new int[][]{{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}});
            default:       return new ArrayList<>();
        }
    }

    private ArrayList<Move> generateRayMoves(Board board, int[][] dirs) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int[] dir : dirs) {
            int c = king.getCol() + dir[0], r = king.getRow() + dir[1];
            while (c >= 0 && c < board.boardX && r >= 0 && r < board.boardY) {
                Tile t = board.getTiles().get(c).get(r);
                if (t.getInteractable() != null && !t.getInteractable().isPassable()) break;
                moves.add(new Move(king, c, r, board));
                if (t.getPiece() != null) break;
                c += dir[0];
                r += dir[1];
            }
        }
        return moves;
    }

    private ArrayList<Move> generateJumpMoves(Board board, int[][] offsets) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int[] off : offsets) {
            int c = king.getCol() + off[0], r = king.getRow() + off[1];
            if (c >= 0 && c < board.boardX && r >= 0 && r < board.boardY) {
                Tile t = board.getTiles().get(c).get(r);
                if (t.getInteractable() == null || t.getInteractable().isPassable()) {
                    moves.add(new Move(king, c, r, board));
                }
            }
        }
        return moves;
    }
}
