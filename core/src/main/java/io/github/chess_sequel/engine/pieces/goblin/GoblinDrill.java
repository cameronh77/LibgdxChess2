package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.Bishop;

import java.util.ArrayList;

/**
 * Goblin faction bishop. Moves diagonally like a Bishop but can pass through (drill through)
 * one blocking piece per diagonal direction, continuing the ray behind it.
 */
public class GoblinDrill extends Bishop {

    public GoblinDrill(int x, int y, boolean isBlack){
        super(x, y, isBlack, "goblin-drill", ChessClass.GOBLIN);
    }

    @Override
    public String getDescription() { return "Moves diagonally like a Bishop, but can drill through one piece in each direction."; }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck){
        if(board instanceof AlterLayoutBoard){
            return generateAlterLayoutMoves(board);
        }

        boolean posPos = false;
        boolean posNeg = false;
        boolean negPos = false;
        boolean negNeg = false;

        ArrayList<Move> moves = new ArrayList<>();
        if(isBlack == board.getWhiteToMove()) {
            //pos pos
            for (int offset = 1; offset + col < board.boardX && offset + row < board.boardY; offset += 1) {
                //if there is a piece
                if (board.getTiles().get(col + offset).get(row + offset).getPiece() != null) {
                    //if the piece is an enemy piece
                    if (board.getTiles().get(col + offset).get(row + offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new Move(this, col + offset, row + offset, board));
                    }
                    if(posPos){
                        break;
                    }
                    posPos = true;
                } else {
                    moves.add(new Move(this, col + offset, row + offset, board));
                }
            }

            //pos neg
            for (int offset = 1; offset + col < board.boardX && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col + offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col + offset).get(row - offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new Move(this, col + offset, row - offset, board));
                    }
                    if(posNeg){
                        break;
                    }
                    posNeg = true;
                } else {
                    moves.add(new Move(this, col + offset, row - offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && offset + row < board.boardY; offset += 1) {
                if (board.getTiles().get(col - offset).get(row + offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row + offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new Move(this, col - offset, row + offset, board));
                    }
                    if(negPos){
                        break;
                    }
                    negPos = true;
                } else {
                    moves.add(new Move(this, col - offset, row + offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col - offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row - offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new Move(this, col - offset, row - offset, board));
                    }
                    if(negNeg){
                        break;
                    }
                    negNeg = true;
                } else {
                    moves.add(new Move(this, col - offset, row - offset, board));
                }
            }

            if(!ignoreCheck){
                ArrayList<Move> trueMoves = new ArrayList<>();
                for (Move move : moves) {
                    if (!board.checkEvaluator(move)) {
                        trueMoves.add(move);
                    }
                }
                return trueMoves;
            }

            return moves;
        } else{
            return moves;
        }

    }
}
