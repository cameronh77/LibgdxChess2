package io.github.chess_sequel.engine.pieces.unassigned;

import io.github.chess_sequel.engine.auras.PetrifyingAura;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.GoblinQueenMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Queen;

import java.util.ArrayList;

/**
 * Reserved queen piece — moves like a standard Queen and places a {@link PetrifyingAura}
 * on all 8 adjacent tiles whenever it moves, removing them from its old position.
 * Held here until assigned to a future class.
 */
public class PetrifyQueen extends Queen {

    public PetrifyQueen(int x, int y, boolean isBlack){
        super(x, y, isBlack, "goblin-queen", ChessClass.GOBLIN);
    }

    @Override
    public String getDescription() { return "Moves like a Queen. Petrifies all adjacent tiles, preventing enemy pieces from moving through them."; }

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
                        moves.add(new GoblinQueenMove(this, col + offset, row + offset, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col + offset, row + offset, board));
                }
            }

            for (int offset = 1; offset + col < board.boardX && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col + offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col + offset).get(row - offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new GoblinQueenMove(this, col + offset, row - offset, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col + offset, row - offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && offset + row < board.boardY; offset += 1) {
                if (board.getTiles().get(col - offset).get(row + offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row + offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new GoblinQueenMove(this, col - offset, row + offset, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col - offset, row + offset, board));
                }
            }

            for (int offset = 1; -offset + col >= 0 && -offset + row >= 0; offset += 1) {
                if (board.getTiles().get(col - offset).get(row - offset).getPiece() != null) {
                    if (board.getTiles().get(col - offset).get(row - offset).getPiece().getIsBlack() != isBlack) {
                        moves.add(new GoblinQueenMove(this, col - offset, row - offset, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col - offset, row - offset, board));
                }
            }

            for (int c = col + 1; c < board.boardX; c += 1) {
                if (board.getTiles().get(c).get(row).getPiece() != null) {
                    if (board.getTiles().get(c).get(row).getPiece().getIsBlack() != isBlack) {
                        moves.add(new GoblinQueenMove(this, c, row, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, c, row, board));
                }
            }

            for (int r = row + 1; r < board.boardY; r += 1) {
                if (board.getTiles().get(col).get(r).getPiece() != null) {
                    if (board.getTiles().get(col).get(r).getPiece().getIsBlack() != isBlack) {
                        moves.add(new GoblinQueenMove(this, col, r, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col, r, board));
                }
            }

            for (int c = col - 1; c >= 0; c -= 1) {
                if (board.getTiles().get(c).get(row).getPiece() != null) {
                    if (board.getTiles().get(c).get(row).getPiece().getIsBlack() != isBlack) {
                        moves.add(new GoblinQueenMove(this, c, row, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, c, row, board));
                }
            }

            for (int r = row - 1; r >= 0; r -= 1) {
                if (board.getTiles().get(col).get(r).getPiece() != null) {
                    if (board.getTiles().get(col).get(r).getPiece().getIsBlack() != isBlack) {
                        moves.add(new GoblinQueenMove(this, col, r, board));
                    }
                    break;
                } else {
                    moves.add(new GoblinQueenMove(this, col, r, board));
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

    private Board activeBoard;

    private void addAurasAround(Board b, int c, int r) {
        if (c - 1 >= 0) {
            b.getTiles().get(c-1).get(r).getAuras().add(new PetrifyingAura(this));
            if (r - 1 >= 0)        b.getTiles().get(c-1).get(r-1).getAuras().add(new PetrifyingAura(this));
            if (r + 1 < b.boardY)  b.getTiles().get(c-1).get(r+1).getAuras().add(new PetrifyingAura(this));
        }
        if (c + 1 < b.boardX) {
            b.getTiles().get(c+1).get(r).getAuras().add(new PetrifyingAura(this));
            if (r - 1 >= 0)        b.getTiles().get(c+1).get(r-1).getAuras().add(new PetrifyingAura(this));
            if (r + 1 < b.boardY)  b.getTiles().get(c+1).get(r+1).getAuras().add(new PetrifyingAura(this));
        }
        if (r - 1 >= 0)       b.getTiles().get(c).get(r-1).getAuras().add(new PetrifyingAura(this));
        if (r + 1 < b.boardY) b.getTiles().get(c).get(r+1).getAuras().add(new PetrifyingAura(this));
    }

    private void removeAurasAround(Board b, int c, int r) {
        if (c - 1 >= 0) {
            b.getTiles().get(c-1).get(r).removeAura(this, "petrifyingAura");
            if (r - 1 >= 0)        b.getTiles().get(c-1).get(r-1).removeAura(this, "petrifyingAura");
            if (r + 1 < b.boardY)  b.getTiles().get(c-1).get(r+1).removeAura(this, "petrifyingAura");
        }
        if (c + 1 < b.boardX) {
            b.getTiles().get(c+1).get(r).removeAura(this, "petrifyingAura");
            if (r - 1 >= 0)        b.getTiles().get(c+1).get(r-1).removeAura(this, "petrifyingAura");
            if (r + 1 < b.boardY)  b.getTiles().get(c+1).get(r+1).removeAura(this, "petrifyingAura");
        }
        if (r - 1 >= 0)       b.getTiles().get(c).get(r-1).removeAura(this, "petrifyingAura");
        if (r + 1 < b.boardY) b.getTiles().get(c).get(r+1).removeAura(this, "petrifyingAura");
    }

    @Override
    public void onStart(Board board){
        this.activeBoard = board;
        addAurasAround(board, col, row);
    }

    @Override
    public void onCapture(Piece piece) {
        if (activeBoard != null) {
            removeAurasAround(activeBoard, col, row);
        }
    }

    @Override
    public void undoOnCapture(Piece piece) {
        if (activeBoard != null) {
            addAurasAround(activeBoard, col, row);
        }
    }
}
