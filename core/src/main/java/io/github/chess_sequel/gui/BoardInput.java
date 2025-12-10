package io.github.chess_sequel.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import io.github.chess_sequel.engine.Game;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

public class BoardInput extends InputAdapter {

    private final GameBoard board;
    private Board inputBoard;

    private OrthographicCamera camera;

    private Piece draggingPiece = null;
    private float dragX, dragY;

    public BoardInput(OrthographicCamera camera, GameBoard board, Board inputBoard) {
        this.camera = camera;
        this.inputBoard = inputBoard;
        this.board = board;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;
        Vector3 mouse = new Vector3(screenX, screenY, 0);
        camera.unproject(mouse);
        int row = (int) mouse.y / board.TILE_SIZE;
        int col = (int) mouse.x / board.TILE_SIZE;
        System.out.println("Clicked");

        Tile tile = inputBoard.getTiles().get(col).get(row);
        System.out.println("Col clicked: "+ col + " Row clicked: " + row);
        System.out.println("Tile X: " + tile.getXord() + " Tile Y: " + tile.getYord());
        System.out.println(tile.getPiece() == null?"It contains no piece":"At this instance it contains piece: " + tile.getPiece().getName());

        if(tile.getPiece() != null){
            inputBoard.setSelectedPiece(tile.getPiece());
            System.out.println("Piece " + tile.getPiece().getName() + " has been selected from col: "+ tile.getPiece().getCol() + " row: " + tile.getPiece().getRow());
            inputBoard.generatePieceMoves();
        }

        // Store drag pixel offsets for smooth dragging
        dragX = mouse.x - board.TILE_SIZE/2f;
        dragY = mouse.y - board.TILE_SIZE/2f;

        return true;


    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(inputBoard.getSelectedPiece() != null){
            Vector3 worldCoords = new Vector3(screenX, screenY, 0);
            camera.unproject(worldCoords);

            // smooth follow with offset
            this.dragX = (worldCoords.x - board.TILE_SIZE/2f);
            this.dragY = (worldCoords.y - board.TILE_SIZE/2f);

        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(inputBoard.getSelectedPiece() != null){

            Vector3 mouse = new Vector3(screenX, screenY, 0);
            camera.unproject(mouse);

            int row = (int) mouse.y / board.TILE_SIZE;
            int col = (int) mouse.x / board.TILE_SIZE;
            Tile tile = inputBoard.getTiles().get(col).get(row);
            System.out.println("Col unClicked: "+ col + " Row unClicked: " + row);
            System.out.println("Tile X: " + tile.getXord() + " Tile Y: " + tile.getYord());
            System.out.println(tile.getPiece() == null?"It contains no piece":"At this instance it contains piece: " + tile.getPiece().getName());

            Move attemptedMove = new Move(inputBoard.getSelectedPiece(), (int) mouse.x, (int) mouse.y, board.game.getCurrentBoard());
            Boolean executed = false;
            for(Move move: inputBoard.getValidMoves()){
                System.out.println("Move new X: "+move.getNewX() + " Move new Y: " + move.getNewY());
            }
            for(Move move: inputBoard.getValidMoves()){
                if(move.getNewX() == attemptedMove.getNewX() && move.getNewY() == attemptedMove.getNewY() && move.getMovingPiece() == attemptedMove.getMovingPiece()){
                    System.out.println("Action executed");
                    move.execute();
                    executed = true;
                    if(inputBoard instanceof MatchBoard){
                        inputBoard.getBotPlayer().takeTurn(inputBoard);
                    }
                    break;
                }
            }
            if(!executed){
                inputBoard.getSelectedPiece().setCol(inputBoard.getSelectedPiece().getCol());
                inputBoard.getSelectedPiece().setRow(inputBoard.getSelectedPiece().getRow());
                System.out.println("Invalid action");
            }
            inputBoard.setSelectedPiece(null);
            inputBoard.resetValidMoves();

        }
        return true;
    }

    public Piece getDraggingPiece() {
        return draggingPiece;
    }

    public float getDragX() { return dragX; }
    public float getDragY() { return dragY; }
}

