package io.github.chess_sequel.engine.pieces.war.loss;

import io.github.chess_sequel.engine.auras.PhylacteryAura;
import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.InteractMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;

import java.util.ArrayList;

/**
 * Loss faction queen. Moves one square in any direction (like a king). While alive it emits a
 * {@link PhylacteryAura} that prevents enemies from targeting the {@link LossKing}'s tile —
 * the king cannot be captured until this piece is destroyed first.
 */
public class PhylacteryQueen extends Piece {

    private Board activeBoard;
    private final PhylacteryAura phylacteryAura = new PhylacteryAura(this);

    public PhylacteryQueen(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "phylactery-queen", ChessClass.LOSS);
        pieceType = PieceType.QUEEN;
    }

    @Override
    public String getDescription() { return "Moves one square in any direction. While alive, the Loss King cannot be captured."; }

    @Override
    public void onStart(Board board) {
        this.activeBoard = board;
        board.addAura(phylacteryAura);
    }

    @Override
    public void onCapture(Piece piece) {
        if (activeBoard != null) activeBoard.removeAura(phylacteryAura);
    }

    @Override
    public void undoOnCapture(Piece piece) {
        if (activeBoard != null) activeBoard.addAura(phylacteryAura);
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        ArrayList<Move> moves = new ArrayList<>();
        if (isBlack != board.getWhiteToMove()) return moves;

        int[][] dirs = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        for (int[] d : dirs) {
            int nc = col + d[0], nr = row + d[1];
            if (nc < 0 || nc >= board.boardX || nr < 0 || nr >= board.boardY) continue;
            Tile dest = board.getTiles().get(nc).get(nr);
            boolean blockedByPiece = dest.getPiece() != null && dest.getPiece().isBlack() == isBlack;
            boolean blockedByTerrain = dest.getInteractable() != null && !dest.getInteractable().isPassable();
            if (!blockedByPiece && !blockedByTerrain) {
                moves.add(new Move(this, nc, nr, board));
            } else if (!blockedByPiece) {
                moves.add(new InteractMove(this, nc, nr, board));
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
