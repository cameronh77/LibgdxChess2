package io.github.chess_sequel.engine.pieces;

import io.github.chess_sequel.engine.auras.Aura;
import io.github.chess_sequel.engine.interactables.Interactable;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.moves.AlterLayoutMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PassiveKingPower;
import io.github.chess_sequel.engine.powers.kingPower.PreKingPower;
import io.github.chess_sequel.engine.powers.pieceAltering.AlterMovePower;

import java.util.ArrayList;
import java.util.List;

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

    protected Board activeBoard;
    private final List<ActiveKingPower>  activePowers  = new ArrayList<>();
    private final List<ActiveKingPower>  benchActivePowers = new ArrayList<>();
    private final List<PassiveKingPower> passivePowers = new ArrayList<>();
    private final List<PreKingPower>     preGamePowers = new ArrayList<>();

    public void addActivePower(ActiveKingPower power) {
        if (activePowers.size() < 3) activePowers.add(power);
        else benchActivePowers.add(power);
    }
    public void addPassivePower(PassiveKingPower power)  { passivePowers.add(power); }
    public void addPreGamePower(PreKingPower power)      { preGamePowers.add(power); }
    public List<ActiveKingPower>  getActivePowers()     { return activePowers; }
    public List<ActiveKingPower>  getBenchActivePowers() { return benchActivePowers; }
    public List<PassiveKingPower> getPassivePowers()    { return passivePowers; }
    public List<PreKingPower>     getPreGamePowers()    { return preGamePowers; }

    public void swapChosen(int i, int j) {
        if (i == j || i >= activePowers.size() || j >= activePowers.size()) return;
        ActiveKingPower tmp = activePowers.get(i);
        activePowers.set(i, activePowers.get(j));
        activePowers.set(j, tmp);
    }

    public void swapChosenWithBench(int chosenIdx, int benchIdx) {
        if (chosenIdx >= activePowers.size() || benchIdx >= benchActivePowers.size()) return;
        ActiveKingPower tmp = activePowers.get(chosenIdx);
        activePowers.set(chosenIdx, benchActivePowers.get(benchIdx));
        benchActivePowers.set(benchIdx, tmp);
    }

    public void moveChosenToBench(int chosenIdx) {
        if (chosenIdx >= activePowers.size()) return;
        benchActivePowers.add(activePowers.remove(chosenIdx));
    }

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

    /**
     * Called when THIS piece is captured by {@code piece}.
     * Base implementation removes any passive powers from the board.
     * Subclasses that override must call {@code super.onCapture(piece)} to preserve power cleanup.
     */
    public void onCapture(Piece piece){
        if (activeBoard == null || passivePowers.isEmpty()) return;
        for (PassiveKingPower power : passivePowers) activeBoard.removeAura(power);
    }

    /**
     * Reverts {@link #onCapture} — called when a move is undone.
     * Subclasses that override must call {@code super.undoOnCapture(piece)}.
     */
    public void undoOnCapture(Piece piece){
        if (activeBoard == null || passivePowers.isEmpty()) return;
        for (PassiveKingPower power : passivePowers) activeBoard.addAura(power);
    }

    /** Convenience alias used by King so it can call its parent's capture logic explicitly. */
    protected final void onCaptured()     { onCapture(null); }
    protected final void undoOnCaptured() { undoOnCapture(null); }

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

    /** Registers passive powers as board auras. Subclasses should call {@code super.onStart(board)} if they override this. */
    public void onStart(Board board){
        this.activeBoard = board;
        for (PassiveKingPower power : passivePowers) board.addAura(power);
    }

    /** Returns a move to execute automatically at the start of this piece's turn, or null if none. */
    public Move onTurnStart(Board board) { return null; }

    /** Called after any move executes on the board. Override to react to other pieces moving. */
    public void postMove(Move move, Board board) {}

    /** Called when any move is undone. Must mirror postMove exactly. */
    public void undoPostMove(Move move, Board board) {}
}
