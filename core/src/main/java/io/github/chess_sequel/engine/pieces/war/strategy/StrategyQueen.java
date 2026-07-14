package io.github.chess_sequel.engine.pieces.war.strategy;

import io.github.chess_sequel.engine.auras.TelegraphAura;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.moves.InteractMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.QueenRegularMove;
import io.github.chess_sequel.engine.moves.TelegraphExecuteMove;
import io.github.chess_sequel.engine.moves.TelegraphPlanMove;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.classic.Queen;

import java.util.ArrayList;

/**
 * War piece — strategy path.
 *
 * Alternates between two modes after each action:
 *   Regular — slides like a normal queen, then enters telegraph mode.
 *   Telegraph — phase 1: marks a destination (visible to opponent). Phase 2: must move there,
 *               capturing whatever is on the tile.
 */
public class StrategyQueen extends Queen {

    private boolean telegraphMode = false;
    private int telegraphCol = -1;
    private int telegraphRow = -1;
    private TelegraphAura pendingAura = null;

    public StrategyQueen(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "strategy-queen", ChessClass.STRATEGY);
    }

    @Override
    public String getDescription() {
        if (!telegraphMode) return "Regular mode — moves like a queen. Next turn enters telegraph mode.";
        if (pendingAura == null) return "Telegraph — select a destination. It will be telegraphed to your opponent.";
        return "Telegraph — must advance to the marked tile.";
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) {
            return generateAlterLayoutMoves(board);
        }

        if (board instanceof MapBoard) {
            return generateMapMoves(board);
        }

        ArrayList<Move> moves = new ArrayList<>();
        if (isBlack != board.getWhiteToMove()) return moves;

        if (telegraphMode && pendingAura != null) {
            if (telegraphCol >= 0 && telegraphCol < board.boardX && telegraphRow >= 0 && telegraphRow < board.boardY) {
                io.github.chess_sequel.engine.location.Tile tile = board.getTiles().get(telegraphCol).get(telegraphRow);
                if (tile.getPiece() == null || tile.getPiece().isBlack() != isBlack) {
                    moves.add(new TelegraphExecuteMove(this, telegraphCol, telegraphRow, pendingAura, board));
                }
            }
        } else if (telegraphMode) {
            for (int c = 0; c < board.boardX; c++) {
                for (int r = 0; r < board.boardY; r++) {
                    if (c == col && r == row) continue;
                    io.github.chess_sequel.engine.location.Tile tile = board.getTiles().get(c).get(r);
                    if (tile.getPiece() != null && tile.getPiece().isBlack() == isBlack) continue;
                    moves.add(new TelegraphPlanMove(this, c, r, board));
                }
            }
        } else {
            moves.addAll(buildQueenReachable(board, false));
        }

        if (!ignoreCheck) {
            ArrayList<Move> safe = new ArrayList<>();
            for (Move m : moves) {
                if (!board.checkEvaluator(m)) safe.add(m);
            }
            return safe;
        }

        return moves;
    }

    private ArrayList<Move> generateMapMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] d : dirs) {
            for (int offset = 1; ; offset++) {
                int c = col + d[0] * offset;
                int r = row + d[1] * offset;
                if (c < 0 || c >= board.boardX || r < 0 || r >= board.boardY) break;
                io.github.chess_sequel.engine.location.Tile tile = board.getTiles().get(c).get(r);
                if (tile.getInteractable() != null && !tile.getInteractable().isPassable()) {
                    moves.add(new InteractMove(this, c, r, board));
                    break;
                }
                if (tile.getPiece() != null && tile.getPiece().isBlack() == isBlack) break;
                moves.add(new Move(this, c, r, board));
                if (tile.getPiece() != null) break;
            }
        }
        return moves;
    }

    private ArrayList<Move> buildQueenReachable(Board board, boolean asTelegraph) {
        ArrayList<Move> moves = new ArrayList<>();
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] d : dirs) {
            for (int offset = 1; ; offset++) {
                int c = col + d[0] * offset;
                int r = row + d[1] * offset;
                if (c < 0 || c >= board.boardX || r < 0 || r >= board.boardY) break;
                io.github.chess_sequel.engine.location.Tile tile = board.getTiles().get(c).get(r);
                if (tile.getPiece() != null && tile.getPiece().isBlack() == isBlack) break;
                moves.add(asTelegraph
                    ? new TelegraphPlanMove(this, c, r, board)
                    : new QueenRegularMove(this, c, r, board));
                if (tile.getPiece() != null) break;
            }
        }
        return moves;
    }

    @Override
    public Move onTurnStart(Board board) {
        if (!telegraphMode || pendingAura == null) return null;
        if (telegraphCol >= 0 && telegraphCol < board.boardX && telegraphRow >= 0 && telegraphRow < board.boardY) {
            io.github.chess_sequel.engine.location.Tile tile = board.getTiles().get(telegraphCol).get(telegraphRow);
            if (tile.getPiece() == null || tile.getPiece().isBlack() != isBlack) {
                return new TelegraphExecuteMove(this, telegraphCol, telegraphRow, pendingAura, board);
            }
        }
        return null;
    }

    public void enterTelegraphMode() { this.telegraphMode = true; }
    public void leaveTelegraphMode() { this.telegraphMode = false; }

    public void setPendingAura(int c, int r, TelegraphAura aura) {
        this.telegraphCol = c;
        this.telegraphRow = r;
        this.pendingAura = aura;
    }

    public void clearPendingAura() {
        this.telegraphCol = -1;
        this.telegraphRow = -1;
        this.pendingAura = null;
    }
}
