package io.github.chess_sequel.engine.pieces.war.loss;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.LossCastleMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.Castle;

import java.util.ArrayList;

/**
 * Loss faction rook. Slides orthogonally like a standard castle, but every tile it vacates
 * becomes a haunted tile — enemy pieces that enter it are slowed (slime effect).
 */
public class LossCastle extends Castle {

    public LossCastle(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "loss-castle", ChessClass.LOSS);
    }

    @Override
    public String getDescription() { return "Moves horizontally or vertically. Leaves a haunted tile on every square it vacates, slowing enemies that step on it."; }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        ArrayList<Move> moves = new ArrayList<>();
        if (isBlack != board.getWhiteToMove()) return moves;

        for (int c = col + 1; c < board.boardX; c++) {
            if (board.getTiles().get(c).get(row).getPiece() != null) {
                if (board.getTiles().get(c).get(row).getPiece().getIsBlack() != isBlack)
                    moves.add(new LossCastleMove(this, c, row, board));
                break;
            } else moves.add(new LossCastleMove(this, c, row, board));
        }
        for (int r = row + 1; r < board.boardY; r++) {
            if (board.getTiles().get(col).get(r).getPiece() != null) {
                if (board.getTiles().get(col).get(r).getPiece().getIsBlack() != isBlack)
                    moves.add(new LossCastleMove(this, col, r, board));
                break;
            } else moves.add(new LossCastleMove(this, col, r, board));
        }
        for (int c = col - 1; c >= 0; c--) {
            if (board.getTiles().get(c).get(row).getPiece() != null) {
                if (board.getTiles().get(c).get(row).getPiece().getIsBlack() != isBlack)
                    moves.add(new LossCastleMove(this, c, row, board));
                break;
            } else moves.add(new LossCastleMove(this, c, row, board));
        }
        for (int r = row - 1; r >= 0; r--) {
            if (board.getTiles().get(col).get(r).getPiece() != null) {
                if (board.getTiles().get(col).get(r).getPiece().getIsBlack() != isBlack)
                    moves.add(new LossCastleMove(this, col, r, board));
                break;
            } else moves.add(new LossCastleMove(this, col, r, board));
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
