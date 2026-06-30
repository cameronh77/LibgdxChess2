package io.github.chess_sequel.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.location.board.AlterLayoutBoard;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.location.board.ShopBoard;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.engine.player.BotPlayer;
import io.github.chess_sequel.engine.powers.kingPower.ActiveKingPower;

import java.util.ArrayList;


public class BoardInput extends InputAdapter {

    private final GameBoard board;
    private GameRun gameRun;

    private Piece draggingPiece = null;
    private float dragX, dragY;
    private BoardActor boardActor;

    private ActiveKingPower selectedPower;
    private ArrayList<Move> pendingPowerMoves;

    private Piece hoveredPiece = null;
    private float hoverScreenX, hoverScreenY;

    public BoardInput(GameBoard board, GameRun gameRun) {
        this.gameRun = gameRun;
        this.board = board;
    }

    public void setBoardActor(BoardActor boardActor){
        this.boardActor = boardActor;
    }

    public void selectPower(ActiveKingPower power) {
        gameRun.getCurrentBoard().setSelectedPiece(null);
        gameRun.getCurrentBoard().resetValidMoves();
        this.selectedPower = power;
        this.pendingPowerMoves = power.generateMoves(gameRun.getCurrentBoard());
    }

    public void cancelPower() {
        selectedPower = null;
        pendingPowerMoves = null;
    }

    public boolean hasSelectedPower() { return selectedPower != null; }
    public ActiveKingPower getSelectedPower() { return selectedPower; }
    public ArrayList<Move> getPendingPowerMoves() { return pendingPowerMoves; }

    private Vector3 screenToBoard(int screenX, int screenY) {
        Vector2 stageCoords = new Vector2(screenX, screenY);
        boardActor.getStage().screenToStageCoordinates(stageCoords);
        Vector2 localCoords = boardActor.stageToLocalCoordinates(stageCoords);
        return new Vector3(localCoords.x, localCoords.y, 0);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameRun.isDialogueActive()) return false;
        if (button != Input.Buttons.LEFT) return false;
        Vector3 mouse = screenToBoard(screenX, screenY);
        System.out.println(mouse.x);
        System.out.println(mouse.y);
        if (mouse.x < 0 || mouse.y < 0 ||
            mouse.x >= board.getPixelWidth() ||
            mouse.y >= board.getPixelHeight()) {
            return false;
        }
        int row = (int) mouse.y / board.TILE_SIZE;
        int col = (int) mouse.x / board.TILE_SIZE;
        System.out.println("Clicked");

        Tile tile = gameRun.getCurrentBoard().getTiles().get(col).get(row);
        System.out.println("Col clicked: "+ col + " Row clicked: " + row);
        System.out.println("Tile X: " + tile.getXord() + " Tile Y: " + tile.getYord());
        System.out.println(tile.getPiece() == null ? "It contains no piece" : "At this instance it contains piece: " + tile.getPiece().getName());

        // Don't select pieces while in power targeting mode
        if (selectedPower == null && tile.getPiece() != null) {
            gameRun.getCurrentBoard().setSelectedPiece(tile.getPiece());
            System.out.println("Piece " + tile.getPiece().getName() + " has been selected from col: "+ tile.getPiece().getCol() + " row: " + tile.getPiece().getRow());
            gameRun.getCurrentBoard().generatePieceMoves();
            if(gameRun.getCurrentBoard().getSelectedPiece().getAlterMovePowers().size() > 0){
                System.out.println(gameRun.getCurrentBoard().getSelectedPiece().getAlterMovePowers().get(0).getDuration());
            }
        }

        dragX = mouse.x - board.TILE_SIZE / 2f;
        dragY = mouse.y - board.TILE_SIZE / 2f;

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (gameRun.isDialogueActive()) return false;
        if(gameRun.getCurrentBoard().getSelectedPiece() != null){
            Vector3 worldCoords = screenToBoard(screenX, screenY);
            this.dragX = (worldCoords.x - board.TILE_SIZE / 2f);
            this.dragY = (worldCoords.y - board.TILE_SIZE / 2f);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        // Power targeting mode — match clicked tile to a pending power move
        if (selectedPower != null) {
            Vector3 mouse = screenToBoard(screenX, screenY);
            if (mouse.x >= 0 && mouse.y >= 0 &&
                mouse.x < board.getPixelWidth() &&
                mouse.y < board.getPixelHeight()) {
                int row = (int) mouse.y / board.TILE_SIZE;
                int col = (int) mouse.x / board.TILE_SIZE;
                for (Move move : pendingPowerMoves) {
                    if (move.getNewX() == col && move.getNewY() == row) {
                        Board boardAtPower = gameRun.getCurrentBoard();
                        move.execute();
                        if (boardAtPower instanceof MatchBoard) {
                            if (move.getCapturedPiece() != null && move.getCapturedPiece() == boardAtPower.getBotPlayer().getLeadPiece()) {
                                ((BotPlayer) boardAtPower.getBotPlayer()).onLeaderCaptured(boardAtPower);
                            } else {
                                boardAtPower.getBotPlayer().takeTurn(boardAtPower);
                            }
                        }
                        if (gameRun.getGameState() != GameState.MATCH_WON) {
                            gameRun.setGameState(GameState.BOARD_STATE_CHANGED);
                        }
                        break;
                    }
                }
            }
            cancelPower();
            return true;
        }

        if(gameRun.getCurrentBoard().getSelectedPiece() != null){

            Vector3 mouse = screenToBoard(screenX, screenY);

            int row = (int) mouse.y / board.TILE_SIZE;
            int col = (int) mouse.x / board.TILE_SIZE;


            if (mouse.x < 0 || mouse.y < 0 ||
                mouse.x >= board.getPixelWidth() ||
                mouse.y >= board.getPixelHeight()) {
                if(gameRun.getCurrentBoard() instanceof AlterLayoutBoard && gameRun.getCurrentBoard().getSelectedPiece() != gameRun.getPlayer().getLeadPiece()){
                    gameRun.getPlayer().getPieceInventory().add(gameRun.getCurrentBoard().getSelectedPiece());
                    gameRun.getPlayer().getPieces().remove(gameRun.getCurrentBoard().getSelectedPiece());
                    gameRun.getCurrentBoard().getTiles().get(gameRun.getCurrentBoard().getSelectedPiece().getCol()).get(gameRun.getCurrentBoard().getSelectedPiece().getRow()).setPiece(null);
                    gameRun.getCurrentBoard().getPieces().remove(gameRun.getCurrentBoard().getSelectedPiece());
                    gameRun.setGameState(GameState.BOARD_STATE_CHANGED);
                } else {
                    gameRun.getCurrentBoard().getSelectedPiece().setCol(gameRun.getCurrentBoard().getSelectedPiece().getCol());
                    gameRun.getCurrentBoard().getSelectedPiece().setRow(gameRun.getCurrentBoard().getSelectedPiece().getRow());
                    System.out.println("Invalid action");
                }
                gameRun.getCurrentBoard().setSelectedPiece(null);
                gameRun.getCurrentBoard().resetValidMoves();
                return true;
            }
            Tile tile = gameRun.getCurrentBoard().getTiles().get(col).get(row);
            System.out.println("Col unClicked: "+ col + " Row unClicked: " + row);
            System.out.println("Tile X: " + tile.getXord() + " Tile Y: " + tile.getYord());
            System.out.println(tile.getPiece() == null ? "It contains no piece" : "At this instance it contains piece: " + tile.getPiece().getName());

            Move attemptedMove = new Move(gameRun.getCurrentBoard().getSelectedPiece(), col, row, board.gameRun.getCurrentBoard());
            Boolean executed = false;
            for(Move move: gameRun.getCurrentBoard().getValidMoves()){
                System.out.println("Move new X: "+move.getNewX() + " Move new Y: " + move.getNewY());
            }
            for(Move move: gameRun.getCurrentBoard().getValidMoves()){
                if(move.getNewX() == attemptedMove.getNewX() && move.getNewY() == attemptedMove.getNewY() && move.getMovingPiece() == attemptedMove.getMovingPiece()){
                    System.out.println("Action executed");
                    Board boardAtMove = gameRun.getCurrentBoard();
                    move.execute();
                    if(boardAtMove instanceof MatchBoard){
                        if (move.getCapturedPiece() != null && move.getCapturedPiece() == boardAtMove.getBotPlayer().getLeadPiece()) {
                            ((BotPlayer) boardAtMove.getBotPlayer()).onLeaderCaptured(boardAtMove);
                        } else {
                            boardAtMove.getBotPlayer().takeTurn(boardAtMove);
                        }
                    }
                    if(boardAtMove instanceof MapBoard){
                        if(boardAtMove.getTiles().get(move.getNewX()).get(move.getNewY()).getInteractable() != null){
                            boardAtMove.getTiles().get(move.getNewX()).get(move.getNewY()).getInteractable().interaction();
                        }
                    }
                    if(boardAtMove instanceof ShopBoard){
                        if(boardAtMove.getTiles().get(move.getNewX()).get(move.getNewY()).getInteractable() != null){
                            boardAtMove.getTiles().get(move.getNewX()).get(move.getNewY()).getInteractable().interaction();
                        }
                    }
                    executed = true;
                    break;
                }
            }
            if(!executed){
                gameRun.getCurrentBoard().getSelectedPiece().setCol(gameRun.getCurrentBoard().getSelectedPiece().getCol());
                gameRun.getCurrentBoard().getSelectedPiece().setRow(gameRun.getCurrentBoard().getSelectedPiece().getRow());
                System.out.println("Invalid action");
            }
            gameRun.getCurrentBoard().setSelectedPiece(null);
            gameRun.getCurrentBoard().resetValidMoves();

        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (boardActor == null) return false;
        Vector3 mouse = screenToBoard(screenX, screenY);
        if (mouse.x < 0 || mouse.y < 0 ||
            mouse.x >= board.getPixelWidth() || mouse.y >= board.getPixelHeight()) {
            hoveredPiece = null;
            return false;
        }
        int col = (int) mouse.x / board.TILE_SIZE;
        int row = (int) mouse.y / board.TILE_SIZE;
        if (col >= gameRun.getCurrentBoard().boardX || row >= gameRun.getCurrentBoard().boardY) {
            hoveredPiece = null;
            return false;
        }
        hoveredPiece = gameRun.getCurrentBoard().getTiles().get(col).get(row).getPiece();
        hoverScreenX = screenX;
        hoverScreenY = screenY;
        return false;
    }

    public Piece getHoveredPiece() { return hoveredPiece; }
    public float getHoverScreenX() { return hoverScreenX; }
    public float getHoverScreenY() { return hoverScreenY; }

    public Piece getDraggingPiece() {
        return draggingPiece;
    }

    public float getDragX() { return dragX; }
    public float getDragY() { return dragY; }

}
