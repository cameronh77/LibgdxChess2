package io.github.chess_sequel.engine.pieces.war.strategy;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.CommanderPawnMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.TrapMove;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.PieceType;
import io.github.chess_sequel.engine.pieces.classic.Horse;

import java.util.ArrayList;

/**
 * War piece — strategy path.
 *
 * Moves like a knight. When selected, adjacent friendly pawns can each make one free move
 * (shown in the valid-move list). Moving a pawn does not end the turn — the player still
 * takes their normal action afterwards with any piece.
 */
public class Commander extends Horse {

    public Commander(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "commander", ChessClass.STRATEGY);
    }

    @Override
    public String getDescription() {
        return "Moves like a knight. Adjacent friendly pawns may each move once for free before the turn ends.";
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) {
            return generateAlterLayoutMoves(board);
        }

        ArrayList<Move> moves = new ArrayList<>();
        if (isBlack != board.getWhiteToMove()) return moves;

        // Own L-shaped moves
        moves.addAll(super.generateBaseMoves(board, ignoreCheck));

        // Free pawn moves for adjacent friendly pawns
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        for (int i = 0; i < 8; i++) {
            int nc = col + dc[i];
            int nr = row + dr[i];
            if (nc < 0 || nc >= board.boardX || nr < 0 || nr >= board.boardY) continue;
            io.github.chess_sequel.engine.pieces.Piece piece = board.getTiles().get(nc).get(nr).getPiece();
            if (piece == null || piece.isBlack() != isBlack || piece.getPieceType() != PieceType.PAWN) continue;

            for (Move pawnMove : piece.generateMoves(board, ignoreCheck)) {
                if (pawnMove instanceof TrapMove) continue;
                moves.add(new CommanderPawnMove(piece, pawnMove.getNewX(), pawnMove.getNewY(), board));
            }
        }

        return moves;
    }
}
