package io.github.chess_sequel.engine.moves;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.powers.kingPower.goblin.BloodFrenzyPassive;

import java.util.ArrayList;

/**
 * Represents a single piece move. Captures the board state needed for full undo support:
 * origin, destination, any captured piece, the previous en-passant tile, and the moving
 * piece's first-move flag. {@link #execute()} and {@link #undo()} must always be called
 * as a matching pair.
 */
public class Move {
    protected int oldX, oldY;
    protected int newX, newY;
    protected Board board;
    protected Piece movingPiece;
    protected Piece capturedPiece;
    protected Boolean isFirstMove;
    protected int[] enPassantTile;
    protected TurnCondition previousCondition;


    public Move(Piece piece, int newX, int newY, Board board){
        this.oldX = piece.getCol();
        this.oldY = piece.getRow();
        this.newX = newX;
        this.newY = newY;
        this.board = board;
        this.movingPiece = piece;
        this.isFirstMove = piece.getIsFirstMove();

        if(newX < board.boardX && newY < board.boardY && newX >= 0 && newY >= 0 && board.getTiles().get(newX).get(newY).getPiece() != null){
            capturedPiece = board.getTiles().get(newX).get(newY).getPiece();
        } else{
            capturedPiece = null;
        }

        this.enPassantTile = board.getEnPassantTile();
    }

    /** Applies the move to the board: moves the piece, removes any capture, updates en-passant state, and flips the turn. */
    public void execute(){
        previousCondition = board.getTurnCondition();

        //Vacate old piece
        board.getTiles().get(oldX).get(oldY).setPiece(null);

        //Remove captured piece from the board
        board.getPieces().remove(capturedPiece);

        //Add the piece being moved to the new tile
        board.getTiles().get(newX).get(newY).setPiece(movingPiece);

        //Set the moving pieces location
        movingPiece.setCol(newX);
        movingPiece.setRow(newY);

        //Alter first move state
        movingPiece.setIsFirstMove(false);

        //Set en passant tile
        if(movingPiece.getName() == "pawn" && Math.abs(oldY-newY)==2){
            int[] enPassantTile = {newX, (oldY+newY)/2};
            board.setEnPassantTile(enPassantTile);
        } else{
            board.setEnPassantTile(null);
        }

        // Flip the turn — skipped for frenzy captures so the same side keeps moving
        boolean frenzyCapture = capturedPiece != null && BloodFrenzyPassive.isActive(board, movingPiece.isBlack());
        if(board instanceof MatchBoard && endsTurn() && !frenzyCapture){
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        if(capturedPiece != null){
            capturedPiece.onCapture(movingPiece);
        }

        for (Aura aura : new ArrayList<>(board.getBoardAuras())) {
            aura.onLand(movingPiece, newX, newY, board);
        }

        // Update Blood Frenzy condition
        if (frenzyCapture) {
            TurnCondition c = board.getTurnCondition();
            if (c == null || c.frenzySide != movingPiece.isBlack()) {
                c = new TurnCondition(movingPiece.isBlack());
            }
            board.setTurnCondition(c.withActor(movingPiece));
        } else if (board.getTurnCondition() != null && board.getTurnCondition().frenzySide == movingPiece.isBlack()) {
            board.setTurnCondition(null);
        }

        board.tick();

        for (Piece piece : new ArrayList<>(board.getPieces())) {
            piece.postMove(this, board);
        }

    }




    /** Fully reverts the move, restoring the board to the exact state before {@link #execute()} was called. */
    public void undo(){
        for (Aura aura : new ArrayList<>(board.getBoardAuras())) {
            aura.onUndoLand(movingPiece, newX, newY, board);
        }

        //Revert Tile ownership
        board.getTiles().get(newX).get(newY).setPiece(capturedPiece);

        //Re add captured pieces to the board
        if(capturedPiece != null) {
            board.getPieces().add(capturedPiece);
        }

        //Set the piece ownership back to old tile
        board.getTiles().get(oldX).get(oldY).setPiece(movingPiece);

        //Revert piece co ordinates to original location
        movingPiece.setCol(oldX);
        movingPiece.setRow(oldY);

        //Revert first move status
        movingPiece.setIsFirstMove(isFirstMove);

        //Revert en passant tile
        board.setEnPassantTile(enPassantTile);

        // Reverse turn flip — mirrors execute's frenzy-capture check
        boolean frenzyCapture = capturedPiece != null && BloodFrenzyPassive.isActive(board, movingPiece.isBlack());
        if(board instanceof MatchBoard && endsTurn() && !frenzyCapture){
            board.setWhiteToMove(!board.getWhiteToMove());
        }

        board.setTurnCondition(previousCondition);

        for (Piece piece : new ArrayList<>(board.getPieces())) {
            piece.undoPostMove(this, board);
        }

        board.untick();

        if(capturedPiece != null){
            capturedPiece.undoOnCapture(movingPiece);
        }


    }

    public Piece getMovingPiece(){
        return movingPiece;
    }

    public int getOldX() { return oldX; }
    public int getOldY() { return oldY; }

    public int getNewX() {
        return newX;
    }

    public int getNewY() {
        return newY;
    }


    public Piece getCapturedPiece(){
        return capturedPiece;
    }

    /** Returns true if this move ends the current player's turn. Override to return false for free actions. */
    public boolean endsTurn() { return true; }
}
