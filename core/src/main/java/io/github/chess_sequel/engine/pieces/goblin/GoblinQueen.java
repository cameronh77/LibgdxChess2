package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.BroodmotherMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.Queen;

import java.util.ArrayList;

/**
 * Goblin faction queen. Moves like a standard Queen but leaves a friendly {@link Goblin} pawn
 * on every square it departs from, steadily growing the horde as it repositions.
 */
public class GoblinQueen extends Queen {

    public GoblinQueen(int x, int y, boolean isBlack){
        super(x, y, isBlack, "goblin-queen", ChessClass.GOBLIN);
    }

    @Override
    public String getDescription() { return "Moves like a Queen. Leaves a Goblin pawn on every square it departs from."; }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck){
        if(board instanceof AlterLayoutBoard){
            return generateAlterLayoutMoves(board);
        }
        ArrayList<Move> moves = new ArrayList<>();
        if(isBlack == board.getWhiteToMove()) {
            for (int offset = 1; offset + col < board.boardX && offset + row < board.boardY; offset += 1) {
                if (board.getTiles().get(col + offset).get(row + offset).getPiece() != null) {
                    if (board.getTiles().get(col + offset).get(row + offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new BroodmotherMove(this, col + offset, row + offset, board));
                    }
                    break;
                } else {
                    moves.add(new BroodmotherMove(this, col + offset, row + offset, board));
                }
            }

            for (int offset = 1; offset + col < board.boardX && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col + offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col + offset).get(row - offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new BroodmotherMove(this, col + offset, row - offset, board));
                    }
                    break;
                } else {
                    moves.add(new BroodmotherMove(this, col + offset, row - offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && offset + row < board.boardY; offset += 1) {
                if (board.getTiles().get(col - offset).get(row + offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row + offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new BroodmotherMove(this, col - offset, row + offset, board));
                    }
                    break;
                } else {
                    moves.add(new BroodmotherMove(this, col - offset, row + offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col - offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row - offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new BroodmotherMove(this, col - offset, row - offset, board));
                    }
                    break;
                } else {
                    moves.add(new BroodmotherMove(this, col - offset, row - offset, board));
                }
            }

            for (int c = col + 1; c < board.boardX; c += 1) {
                if (board.getTiles().get(c).get(row).getPiece() != null) {
                    if (board.getTiles().get(c).get(row).getPiece().getIsBlack() != isBlack) {
                        moves.add(new BroodmotherMove(this, c, row, board));
                    }
                    break;
                } else {
                    moves.add(new BroodmotherMove(this, c, row, board));
                }
            }

            for (int r = row + 1; r < board.boardY; r += 1) {
                if (board.getTiles().get(col).get(r).getPiece() != null) {
                    if (board.getTiles().get(col).get(r).getPiece().getIsBlack() != isBlack) {
                        moves.add(new BroodmotherMove(this, col, r, board));
                    }
                    break;
                } else {
                    moves.add(new BroodmotherMove(this, col, r, board));
                }
            }

            for (int c = col - 1; c >= 0; c -= 1) {
                if (board.getTiles().get(c).get(row).getPiece() != null) {
                    if (board.getTiles().get(c).get(row).getPiece().getIsBlack() != isBlack) {
                        moves.add(new BroodmotherMove(this, c, row, board));
                    }
                    break;
                } else {
                    moves.add(new BroodmotherMove(this, c, row, board));
                }
            }

            for (int r = row - 1; r >= 0; r -= 1) {
                if (board.getTiles().get(col).get(r).getPiece() != null) {
                    if (board.getTiles().get(col).get(r).getPiece().getIsBlack() != isBlack) {
                        moves.add(new BroodmotherMove(this, col, r, board));
                    }
                    break;
                } else {
                    moves.add(new BroodmotherMove(this, col, r, board));
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
        } else {
            return moves;
        }
    }
}
