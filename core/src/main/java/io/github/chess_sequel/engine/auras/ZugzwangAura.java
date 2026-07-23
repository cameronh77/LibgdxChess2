package io.github.chess_sequel.engine.auras;

import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

import java.util.ArrayList;

public class ZugzwangAura extends Aura {

    private final Piece forcedPiece;
    // Counter instead of boolean so onUndoLand correctly re-activates the constraint
    // when the opponent's forced move is rewound during minimax.
    private int opponentMoveCount = 0;

    public ZugzwangAura(Piece owner, Piece forcedPiece) {
        super(owner, "ZugzwangAura");
        this.forcedPiece = forcedPiece;
    }

    @Override
    public ArrayList<Move> alterMoves(Piece piece, ArrayList<Move> moves, Board board, Boolean isCheck) {
        if (opponentMoveCount > 0) return moves;              // opponent already took their forced turn
        if (piece.isBlack() == owner.isBlack()) return moves; // only restrict enemy pieces
        if (isCheck) return moves;                            // must be able to respond to check
        if (piece == forcedPiece) return moves;               // this is the piece they must move
        moves.clear();
        return moves;
    }

    @Override
    public void onLand(Piece piece, int landedX, int landedY, Board board) {
        if (piece.isBlack() != owner.isBlack()) opponentMoveCount++;
    }

    @Override
    public void onUndoLand(Piece piece, int landedX, int landedY, Board board) {
        if (piece.isBlack() != owner.isBlack()) opponentMoveCount--;
    }
}
