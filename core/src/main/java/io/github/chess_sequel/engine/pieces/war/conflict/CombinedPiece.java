package io.github.chess_sequel.engine.pieces.war.conflict;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Horse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The result of a friendly piece absorbing a Cavalry. Generates moves by temporarily
 * syncing a Horse instance and the absorbed piece to the current position, combining
 * both move sets (deduplicating by destination square).
 */
public class CombinedPiece extends Piece {

    private final Piece absorbed;

    public CombinedPiece(int col, int row, boolean isBlack, Piece absorbed) {
        super(col, row, isBlack, "cavalry-rider", ChessClass.CONFLICT);
        this.pieceType = absorbed.getPieceType();
        this.absorbed = absorbed;
    }

    @Override
    public String getDescription() {
        return "Moves like a " + absorbed.getName() + " and a horse combined.";
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);
        if (isBlack != board.getWhiteToMove()) return new ArrayList<>();

        // Horse moves
        Horse tempHorse = new Horse(col, row, isBlack, "combined-horse", ChessClass.CONFLICT);
        tempHorse.setStartCords();
        ArrayList<Move> horseMoves = tempHorse.generateBaseMoves(board, true);

        // Absorbed piece moves — sync position to current location
        absorbed.setCol(col);
        absorbed.setRow(row);
        ArrayList<Move> absorbedMoves = absorbed.generateBaseMoves(board, true);

        // Merge, deduplicating by destination
        Set<String> seen = new HashSet<>();
        ArrayList<Move> combined = new ArrayList<>();
        for (Move m : horseMoves) {
            if (seen.add(m.getNewX() + "," + m.getNewY())) combined.add(m);
        }
        for (Move m : absorbedMoves) {
            if (seen.add(m.getNewX() + "," + m.getNewY())) combined.add(m);
        }

        // Remap moves so movingPiece is this CombinedPiece, not the temp instances
        ArrayList<Move> remapped = new ArrayList<>();
        for (Move m : combined) {
            remapped.add(new Move(this, m.getNewX(), m.getNewY(), board));
        }

        if (!ignoreCheck) {
            ArrayList<Move> trueMoves = new ArrayList<>();
            for (Move move : remapped) {
                if (!board.checkEvaluator(move)) trueMoves.add(move);
            }
            return trueMoves;
        }
        return remapped;
    }
}
