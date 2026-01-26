package io.github.chess_sequel.engine.pieces.goblin;

import io.github.chess_sequel.engine.auras.PetrifyingAura;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.GoblinQueenMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.Queen;

import java.util.ArrayList;

public class GoblinQueen extends Queen {

    public GoblinQueen(int x, int y, boolean isWhite){
        super(x, y, isWhite, "goblin-queen", ChessClass.GOBLIN);
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck){
        if(board instanceof AlterLayoutBoard){
            return generateAlterLayoutMoves(board);
        }
        ArrayList<Move> moves = new ArrayList<>();
        if(isWhite == board.getWhiteToMove()) {
            //pos pos
            for (int offset = 1; offset + col < board.boardX && offset + row < board.boardY; offset += 1) {
                if (board.getTiles().get(col + offset).get(row + offset).getPiece() != null) {
                    if (board.getTiles().get(col + offset).get(row + offset).getPiece().getIsWhite() != isWhite) {
                        moves.add(new GoblinQueenMove(this, col + offset, row + offset, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col + offset, row + offset, board));
                }
            }

            //pos neg
            for (int offset = 1; offset + col < board.boardX && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col + offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col + offset).get(row - offset).getPiece().getIsWhite() != isWhite) {
                        moves.add(new GoblinQueenMove(this, col + offset, row - offset, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col + offset, row - offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && offset + row < board.boardY; offset += 1) {
                if (board.getTiles().get(col - offset).get(row + offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row + offset).getPiece().getIsWhite() != isWhite) {
                        moves.add(new GoblinQueenMove(this, col - offset, row + offset, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col - offset, row + offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col - offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row - offset).getPiece().getIsWhite() != isWhite) {
                        moves.add(new GoblinQueenMove(this, col - offset, row - offset, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col - offset, row - offset, board));
                }
            }

            //pos x
            for (int c = col + 1; c < board.boardX; c += 1) {
                if (board.getTiles().get(c).get(row).getPiece() != null) {
                    if (board.getTiles().get(c).get(row).getPiece().getIsWhite() != isWhite) {
                        moves.add(new GoblinQueenMove(this, c, row, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, c, row, board));
                }
            }

            //pos y
            for (int r = row + 1; r < board.boardY; r += 1) {
                if (board.getTiles().get(col).get(r).getPiece() != null) {
                    if (board.getTiles().get(col).get(r).getPiece().getIsWhite() != isWhite) {
                        moves.add(new GoblinQueenMove(this, col, r, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col, r, board));
                }
            }

            //neg x
            for (int c = col - 1; c >= 0; c -= 1) {
                if (board.getTiles().get(c).get(row).getPiece() != null) {
                    if (board.getTiles().get(c).get(row).getPiece().getIsWhite() != isWhite) {
                        moves.add(new GoblinQueenMove(this, c, row, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, c, row, board));
                }
            }

            //neg y
            for (int r = row - 1; r >= 0; r -= 1) {
                if (board.getTiles().get(col).get(r).getPiece() != null) {
                    if (board.getTiles().get(col).get(r).getPiece().getIsWhite() != isWhite) {
                        moves.add(new GoblinQueenMove(this, col, r, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col, r, board));
                }
            }
            if(!ignoreCheck){
                //Ignore the naming convention for now
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

    @Override
    public void onStart(Board board){
        if(col-1 < board.boardX && col-1 >=0){
            board.getTiles().get(col -1).get(row).getAuras().add(new PetrifyingAura(this));

            if(row-1 < board.boardY && row-1 >= 0){
                board.getTiles().get(col-1).get(row-1).getAuras().add(new PetrifyingAura(this));
            }

            if(row+1 < board.boardY && row-1 >= 0){
                board.getTiles().get(col-1).get(row+1).getAuras().add(new PetrifyingAura(this));
            }
        }

        if(col+1 < board.boardX && col+1 >= 0){
            board.getTiles().get(col +1).get(row).getAuras().add(new PetrifyingAura(this));

            if(row-1 < board.boardY && row-1 >= 0){
                board.getTiles().get(col+1).get(row-1).getAuras().add(new PetrifyingAura(this));
            }

            if(row+1 < board.boardY && row+1 >= 0){
                board.getTiles().get(col+1).get(row+1).getAuras().add(new PetrifyingAura(this));
            }
        }

        if(row -1 < board.boardY && row-1 >= 0){
            board.getTiles().get(col).get(row-1).getAuras().add(new PetrifyingAura(this));
        }

        if(row +1 < board.boardY && row+1 >= 0){
            board.getTiles().get(col).get(row+1).getAuras().add(new PetrifyingAura(this));
        }
    }
}
