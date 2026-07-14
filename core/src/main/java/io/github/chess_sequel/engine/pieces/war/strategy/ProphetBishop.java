package io.github.chess_sequel.engine.pieces.war.strategy;

import io.github.chess_sequel.engine.auras.ProphetPathAura;
import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.CommitMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.moves.PlanningMove;
import io.github.chess_sequel.engine.pieces.ChessClass;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.pieces.classic.Bishop;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * War piece — strategy path.
 *
 * On its turn the bishop plans up to 3 diagonal moves (clicking its own tile commits early).
 * The 3rd planned step must return to the bishop's current position. After committing, each
 * time any other piece moves the bishop automatically executes its next queued step via
 * {@link #postMove}. The planned path is visible to both players via path markers.
 *
 * Interruption: blocked by terrain or a friendly piece → stops short. Blocked by an enemy →
 * captures it and stops. All remaining queued steps are cancelled on interruption.
 */
public class ProphetBishop extends Bishop {

    // Built up during the planning turn (not yet committed)
    public final ArrayList<int[]> planningSteps = new ArrayList<>();
    public final ArrayList<ProphetPathAura> planningMarkers = new ArrayList<>();

    // Committed queue consumed one step per other-piece-move
    public final ArrayList<int[]> scryQueue = new ArrayList<>();
    public final ArrayList<ProphetPathAura> scryMarkers = new ArrayList<>();

    // Undo records for postMove auto-steps
    private final Deque<ScryRecord> undoStack = new ArrayDeque<>();

    public ProphetBishop(int x, int y, boolean isBlack) {
        super(x, y, isBlack, "prophet-bishop", ChessClass.STRATEGY);
    }

    @Override
    public String getDescription() {
        if (!scryQueue.isEmpty())
            return "Scrying — " + scryQueue.size() + " step(s) remaining. Triggers on each enemy or ally move.";
        if (!planningSteps.isEmpty())
            return "Planning — queue up to " + (3 - planningSteps.size()) + " more step(s), or click this square to commit.";
        return "Plans up to 3 diagonal moves. Each subsequent piece move triggers the next step automatically.";
    }

    @Override
    public ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck) {
        if (board instanceof AlterLayoutBoard) return generateAlterLayoutMoves(board);

        ArrayList<Move> moves = new ArrayList<>();
        if (isBlack != board.getWhiteToMove()) return moves;
        if (!scryQueue.isEmpty()) return moves; // on autopilot

        // Clicking the bishop's own tile commits (or wastes turn if nothing queued)
        moves.add(new CommitMove(this, board));

        if (planningSteps.size() < 3) {
            int[] sim = simulatedPosition();
            int simC = sim[0], simR = sim[1];

            int[][] dirs = {{1,1},{1,-1},{-1,1},{-1,-1}};
            for (int[] d : dirs) {
                for (int offset = 1; ; offset++) {
                    int c = simC + d[0] * offset;
                    int r = simR + d[1] * offset;
                    if (c < 0 || c >= board.boardX || r < 0 || r >= board.boardY) break;
                    Tile tile = board.getTiles().get(c).get(r);
                    if (tile.getPiece() != null && tile.getPiece().isBlack() == isBlack) break;
                    moves.add(new PlanningMove(this, c, r, board));
                    if (tile.getPiece() != null) break;
                }
            }
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

    @Override
    public void postMove(Move move, Board board) {
        if (scryQueue.isEmpty()) return;
        if (move.getMovingPiece() == this) return;
        if (move.getMovingPiece().isBlack() != isBlack) return; // only friendly moves trigger

        int[] target = scryQueue.get(0);
        int targetC = target[0], targetR = target[1];
        int dc = Integer.signum(targetC - col);
        int dr = Integer.signum(targetR - row);

        int endC = col, endR = row;
        Piece captured = null;
        boolean interrupted = false;

        int c = col, r = row;
        while (c != targetC || r != targetR) {
            int nc = c + dc, nr = r + dr;
            Tile tile = board.getTiles().get(nc).get(nr);

            if (tile.getInteractable() != null && !tile.getInteractable().isPassable()) {
                interrupted = true;
                break;
            }
            if (tile.getPiece() != null) {
                if (tile.getPiece().isBlack() == isBlack) {
                    interrupted = true;
                } else {
                    endC = nc; endR = nr;
                    captured = tile.getPiece();
                    interrupted = true;
                }
                break;
            }
            endC = nc; endR = nr;
            c = nc; r = nr;
        }

        // Consume first step and its marker
        ArrayList<int[]> consumed = new ArrayList<>();
        ArrayList<ProphetPathAura> consumedMarkers = new ArrayList<>();
        consumed.add(scryQueue.remove(0));
        consumedMarkers.add(scryMarkers.remove(0));
        board.removeAura(consumedMarkers.get(0));

        if (interrupted) {
            consumed.addAll(scryQueue);
            consumedMarkers.addAll(scryMarkers);
            for (ProphetPathAura m : scryMarkers) board.removeAura(m);
            scryQueue.clear();
            scryMarkers.clear();
        }

        undoStack.push(new ScryRecord(move, col, row, captured, endC, endR, consumed, consumedMarkers));

        board.getTiles().get(col).get(row).setPiece(null);
        if (captured != null) board.getPieces().remove(captured);
        board.getTiles().get(endC).get(endR).setPiece(this);
        col = endC;
        row = endR;
    }

    @Override
    public void undoPostMove(Move move, Board board) {
        if (undoStack.isEmpty()) return;
        ScryRecord top = undoStack.peek();
        if (top.triggeringMove != move) return;
        undoStack.pop();

        board.getTiles().get(col).get(row).setPiece(null);
        if (top.captured != null) {
            board.getTiles().get(top.endC).get(top.endR).setPiece(top.captured);
            board.getPieces().add(top.captured);
            top.captured.setCol(top.endC);
            top.captured.setRow(top.endR);
        }
        board.getTiles().get(top.prevC).get(top.prevR).setPiece(this);
        col = top.prevC;
        row = top.prevR;

        for (int i = top.consumed.size() - 1; i >= 0; i--) {
            scryQueue.add(0, top.consumed.get(i));
            scryMarkers.add(0, top.consumedMarkers.get(i));
            board.addAura(top.consumedMarkers.get(i));
        }
    }

    private int[] simulatedPosition() {
        if (planningSteps.isEmpty()) return new int[]{col, row};
        return planningSteps.get(planningSteps.size() - 1);
    }

    private static class ScryRecord {
        final Move triggeringMove;
        final int prevC, prevR, endC, endR;
        final Piece captured;
        final ArrayList<int[]> consumed;
        final ArrayList<ProphetPathAura> consumedMarkers;

        ScryRecord(Move triggeringMove, int prevC, int prevR, Piece captured,
                   int endC, int endR, ArrayList<int[]> consumed,
                   ArrayList<ProphetPathAura> consumedMarkers) {
            this.triggeringMove = triggeringMove;
            this.prevC = prevC; this.prevR = prevR;
            this.endC = endC; this.endR = endR;
            this.captured = captured;
            this.consumed = consumed;
            this.consumedMarkers = consumedMarkers;
        }
    }
}
