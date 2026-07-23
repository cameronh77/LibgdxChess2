package io.github.chess_sequel.engine.pieces.war.loss;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

/**
 * Loss faction pawn. Moves like a standard pawn. When captured, it respawns on its
 * starting tile at the moment of capture — if that tile is already occupied the respawn
 * does not occur and the piece is permanently lost.
 */
public class LossPawn extends Piece {

    private Board activeBoard;
    private int captureCol, captureRow;
    private boolean didRespawn = false;

    public LossPawn(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "loss-pawn", ChessClass.LOSS);
        pieceType = PieceType.PAWN;
    }

    @Override
    public String getDescription() { return "Moves forward. When captured, respawns on its starting tile if unoccupied."; }

    @Override
    public void onStart(Board board) {
        this.activeBoard = board;
    }

    @Override
    public void onCapture(Piece attacker) {
        captureCol = col;
        captureRow = row;
        if (activeBoard != null && activeBoard.getTiles().get(trueCol).get(trueRow).getPiece() == null) {
            activeBoard.getTiles().get(trueCol).get(trueRow).setPiece(this);
            activeBoard.getPieces().add(this);
            this.col = trueCol;
            this.row = trueRow;
            didRespawn = true;
        }
    }

    @Override
    public void undoOnCapture(Piece attacker) {
        if (didRespawn) {
            // Move.undo() has already re-added us to pieces and restored our tile at (captureCol, captureRow).
            // We clear the respawn tile and remove the extra pieces-list entry from the respawn.
            activeBoard.getTiles().get(trueCol).get(trueRow).setPiece(null);
            activeBoard.getPieces().remove(this);
            this.col = captureCol;
            this.row = captureRow;
            didRespawn = false;
        }
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        ArrayList<Move> moves = new ArrayList<>();
        if (isBlack != board.getWhiteToMove()) return moves;

        int dir = isBlack ? -1 : 1;
        int fRow = row + dir;

        // Forward 1
        if (fRow >= 0 && fRow < board.boardY && board.getTiles().get(col).get(fRow).getPiece() == null) {
            moves.add(new Move(this, col, fRow, board));
            // Forward 2 on first move
            int f2Row = row + 2 * dir;
            if (isFirstMove && f2Row >= 0 && f2Row < board.boardY
                    && board.getTiles().get(col).get(f2Row).getPiece() == null) {
                moves.add(new Move(this, col, f2Row, board));
            }
        }

        // Diagonal captures
        for (int dc : new int[]{-1, 1}) {
            int nc = col + dc;
            if (nc >= 0 && nc < board.boardX && fRow >= 0 && fRow < board.boardY) {
                Piece target = board.getTiles().get(nc).get(fRow).getPiece();
                if (target != null && target.isBlack() != isBlack) {
                    moves.add(new Move(this, nc, fRow, board));
                }
            }
        }

        if (!ignoreCheck) {
            ArrayList<Move> trueMoves = new ArrayList<>();
            for (Move move : moves) {
                if (!board.checkEvaluator(move)) trueMoves.add(move);
            }
            return trueMoves;
        }
        return moves;
    }
}
