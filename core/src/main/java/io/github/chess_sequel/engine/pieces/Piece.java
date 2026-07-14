package io.github.chess_sequel.engine.pieces;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.AlterLayoutMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.powers.pieceAltering.AlterMovePower;

import java.util.ArrayList;

/**
 * Base class for all chess pieces. Tracks board position ({@code col}/{@code row}),
 * a "true" starting position used to reset after setup, team colour, and a list of
 * {@link AlterMovePower} modifiers that can change or suppress its moves each turn.
 */
public abstract class Piece {
    protected String name;
    protected String filePath;
    protected Boolean isFirstMove = true;
    protected ArrayList<AlterMovePower> alterMovePowers = new ArrayList<>();

    public boolean isBlack() {
        return isBlack;
    }

    protected int col, row; // 0-7
    protected int trueCol, trueRow;
    protected int mapCol, mapRow;
    protected boolean isBlack;

    protected PieceType pieceType;
    protected ChessClass chessClass;

    public Piece(int x, int y, boolean isBlack, String name, ChessClass chessClass){
        this.trueCol = x;
        this.trueRow = y;

        this.isBlack = isBlack;
        this.name = name;
        this.chessClass = chessClass;
        this.filePath = "pieces/"+chessClass.getType()+"/"+(isBlack?"black":"white")+"-"+name+".png";
    }


    /**
     * Returns all legal moves for this piece. Calls {@link #generateBaseMoves} then lets
     * each {@link AlterMovePower} and tile/board {@link io.github.chess_sequel.engine.auras.Aura} filter the list.
     *
     * @param ignoreCheck when {@code true} skip the self-check filter (used during check detection to avoid recursion)
     */
    public ArrayList<Move> generateMoves(Board board, Boolean ignoreCheck){
        ArrayList<Move> moves = generateBaseMoves(board, ignoreCheck);

        for(AlterMovePower power: alterMovePowers){
            moves = power.alterMoves(moves, board, ignoreCheck);
        }
        for(Aura aura: new ArrayList<>(board.getTiles().get(col).get(row).getAuras())){
            moves = aura.alterMoves(this, moves, board, ignoreCheck);
        }

        for(Aura aura: new ArrayList<>(board.getBoardAuras())){
            moves = aura.alterMoves(this, moves, board, ignoreCheck);
        }

        return moves;
    }

    public abstract ArrayList<Move> generateBaseMoves(Board board, Boolean ignoreCheck);

    /** Generates moves for the layout screen — any empty tile in the bottom half of the board. */
    public ArrayList<Move> generateAlterLayoutMoves(Board board){
        ArrayList<Move> moves = new ArrayList<>();
        for(int x = 0; x< board.boardX;x++){
            for(int y = 0; y < board.boardY/2;y++){
                if(board.getTiles().get(x).get(y).getPiece() == null){
                    moves.add(new AlterLayoutMove(this, x, y, board));
                }
            }
        }
        return moves;
    }

    public void setIsFirstMove(Boolean state){
        isFirstMove = state;
    }

    public Boolean getIsFirstMove(){
        return isFirstMove;
    }

    public Boolean getIsBlack(){
        return isBlack;
    }

    public void setCol(int col){
        this.col = col;
    }
    public int getCol(){
        return col;
    }

    public void setRow(int row){
        this.row = row;
    }

    public int getRow(){
        return row;
    }

    /** Resets {@code col}/{@code row} to the stored true start coordinates (called on board setup). */
    public void setStartCords(){
        //System.out.println("THIS is the true row: "+trueRow + " THis is the true col: "+ trueCol);
        this.row = trueRow;
        this.col = trueCol;
    }

    /** Updates the true start coordinates to the current position (called after a layout move is confirmed). */
    public void updateStartCords(){
        this.trueRow = row;
        this.trueCol = col;
    }

    public String getName(){
        return name;
    }

    public String getDescription() { return null; }

    public String getFilePath() {
        return filePath;
    }

    public PieceType getPieceType(){
        return pieceType;
    }

    public int getTrueCol(){
        return trueCol;
    }

    public int getTrueRow(){
        return trueRow;
    }

    /** Called when this piece captures {@code piece}. Override to apply on-capture effects. */
    public void onCapture(Piece piece){

    }

    /** Reverts any effect applied in {@link #onCapture} — called when a move is undone. */
    public void undoOnCapture(Piece piece){

    }

    public ArrayList<AlterMovePower> getAlterMovePowers(){
        return alterMovePowers;
    }

    public void tick(){
        for(AlterMovePower alterMovePower: alterMovePowers){
            alterMovePower.tick();
        }
    }

    public void untick(){
        for(AlterMovePower alterMovePower: alterMovePowers){
            alterMovePower.untick();
        }
    }

    /**
     * If the piece needs to do anything at the start of the match
     */
    public void onStart(Board board){

    }

    /** Returns a move to execute automatically at the start of this piece's turn, or null if none. */
    public Move onTurnStart(Board board) { return null; }

    /** Called after any move executes on the board. Override to react to other pieces moving. */
    public void postMove(Move move, Board board) {}

    /** Called when any move is undone. Must mirror postMove exactly. */
    public void undoPostMove(Move move, Board board) {}
}
