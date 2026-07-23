package io.github.chess_sequel.engine.pieces.war.conflict;

import io.github.chess_sequel.engine.auras.BarbarianFrenzyAura;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.BarbarianCapture;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.Promotion;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.PieceType;
import io.github.chess_sequel.engine.moves.EnPassant;

import java.util.ArrayList;

/**
 * Conflict pawn. Moves like a standard pawn but uses BarbarianCapture for diagonal takes:
 * if the capture lands a kill, the Barbarian's turn does not end and all other friendly
 * pieces are locked via TurnCondition, letting only the Barbarian move again.
 */
public class Barbarian extends Piece {

    private Board activeBoard;
    private final BarbarianFrenzyAura frenzyAura = new BarbarianFrenzyAura(this);

    public Barbarian(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "barbarian", ChessClass.CONFLICT);
        pieceType = PieceType.PAWN;
    }

    @Override
    public String getDescription() {
        return "Moves like a pawn. On capture, may move again — only the Barbarian acts during this bonus turn.";
    }

    @Override
    public void onStart(Board board) {
        activeBoard = board;
        board.addAura(frenzyAura);
    }

    @Override
    public void onCapture(Piece piece) {
        if (activeBoard != null) activeBoard.removeAura(frenzyAura);
    }

    @Override
    public void undoOnCapture(Piece piece) {
        if (activeBoard != null) activeBoard.addAura(frenzyAura);
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        ArrayList<Move> moves = new ArrayList<>();
        if (isBlack != board.getWhiteToMove()) return moves;

        int offset = isBlack ? -1 : 1;

        // Double advance on first move
        if (isFirstMove && row + 2 * offset > 0 && row + 2 * offset < board.boardY) {
            if (board.getTiles().get(col).get(row + 2 * offset).getPiece() == null
                    && board.getTiles().get(col).get(row + offset).getPiece() == null) {
                moves.add(new Move(this, col, row + 2 * offset, board));
            }
        }

        // Single advance
        if (row + offset > 0 && row + offset < board.boardY
                && board.getTiles().get(col).get(row + offset).getPiece() == null) {
            if (row + offset == (isBlack ? 0 : board.boardY - 1)) {
                moves.add(new Promotion(this, col, row + offset, board, PieceType.QUEEN));
                moves.add(new Promotion(this, col, row + offset, board, PieceType.BISHOP));
                moves.add(new Promotion(this, col, row + offset, board, PieceType.CASTLE));
                moves.add(new Promotion(this, col, row + offset, board, PieceType.HORSE));
            } else {
                moves.add(new Move(this, col, row + offset, board));
            }
        }

        // Left capture (BarbarianCapture so a kill grants another turn)
        if (row + offset > 0 && row + offset < board.boardY && col - 1 >= 0) {
            Piece target = board.getTiles().get(col - 1).get(row + offset).getPiece();
            if (target != null && target.getIsBlack() != isBlack) {
                if (row + offset == (isBlack ? 0 : board.boardY - 1)) {
                    moves.add(new Promotion(this, col - 1, row + offset, board, PieceType.QUEEN));
                    moves.add(new Promotion(this, col - 1, row + offset, board, PieceType.BISHOP));
                    moves.add(new Promotion(this, col - 1, row + offset, board, PieceType.CASTLE));
                    moves.add(new Promotion(this, col - 1, row + offset, board, PieceType.HORSE));
                } else {
                    moves.add(new BarbarianCapture(this, col - 1, row + offset, board));
                }
            }
        }

        // Right capture
        if (row + offset > 0 && row + offset < board.boardY && col + 1 < board.boardX) {
            Piece target = board.getTiles().get(col + 1).get(row + offset).getPiece();
            if (target != null && target.getIsBlack() != isBlack) {
                if (row + offset == (isBlack ? 0 : board.boardY - 1)) {
                    moves.add(new Promotion(this, col + 1, row + offset, board, PieceType.QUEEN));
                    moves.add(new Promotion(this, col + 1, row + offset, board, PieceType.BISHOP));
                    moves.add(new Promotion(this, col + 1, row + offset, board, PieceType.CASTLE));
                    moves.add(new Promotion(this, col + 1, row + offset, board, PieceType.HORSE));
                } else {
                    moves.add(new BarbarianCapture(this, col + 1, row + offset, board));
                }
            }
        }

        // En passant
        if (board.getEnPassantTile() != null) {
            int[] ep = board.getEnPassantTile();
            if (col - 1 == ep[0] && row + offset == ep[1]) {
                moves.add(new EnPassant(this, col - 1, row + offset, board));
            }
            if (col + 1 == ep[0] && row + offset == ep[1]) {
                moves.add(new EnPassant(this, col + 1, row + offset, board));
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
