package io.github.chess_sequel.engine.powers.kingPower.classic;

import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.kingPower.KingPower;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.EnPassant;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;
import io.github.chess_sequel.engine.pieces.classic.Pawn;

import java.util.ArrayList;

/**
 * Passive king power: "All Passant". Friendly pawns may en-passant any horizontally
 * adjacent enemy pawn at any time, regardless of whether that pawn just moved two squares.
 * Standard en-passant (via {@code enPassantTile}) is already in the base move list;
 * this only adds captures not already present.
 */
public class AllPassantPassive extends PassiveKingPower {

    public AllPassantPassive(Piece king) {
        super(king, "All Passant");
    }

    @Override
    public String getIconPath() { return "kingPowers/all-passant.png"; }

    @Override
    public String getDescription() { return "Your Pawns can en passant any adjacent enemy Pawn, regardless of when it moved."; }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean ignoreCheck) {
        if (!(piece instanceof Pawn)) return moves;
        if (piece.getIsBlack() != owner.getIsBlack()) return moves;
        if (piece.getIsBlack() != board.getWhiteToMove()) return moves;

        int col = piece.getCol();
        int row = piece.getRow();
        int offset = piece.getIsBlack() ? -1 : 1;
        int destRow = row + offset;

        if (destRow < 0 || destRow >= board.boardY) return moves;

        ArrayList<Move> extra = new ArrayList<>();
        for (int dc : new int[]{-1, 1}) {
            int adjCol = col + dc;
            if (adjCol < 0 || adjCol >= board.boardX) continue;
            Piece adjacent = board.getTiles().get(adjCol).get(row).getPiece();
            if (adjacent == null) continue;
            if (adjacent.getIsBlack() == piece.getIsBlack()) continue;
            if (adjacent.getPieceType() != PieceType.PAWN) continue;
            // Destination square must be empty (we're moving diagonally through it)
            if (board.getTiles().get(adjCol).get(destRow).getPiece() != null) continue;
            // Skip if already in move list (standard en passant covers this)
            boolean dup = false;
            for (Move m : moves) {
                if (m.getNewX() == adjCol && m.getNewY() == destRow) { dup = true; break; }
            }
            if (!dup) extra.add(new EnPassant(piece, adjCol, destRow, board));
        }

        if (!ignoreCheck) {
            ArrayList<Move> safe = new ArrayList<>();
            for (Move m : extra) {
                if (!board.checkEvaluator(m)) safe.add(m);
            }
            extra = safe;
        }

        moves.addAll(extra);
        return moves;
    }
}
