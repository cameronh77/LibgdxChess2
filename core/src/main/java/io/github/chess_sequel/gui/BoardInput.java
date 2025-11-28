package io.github.chess_sequel.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import io.github.chess_sequel.engine.location.Board;
import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

public class BoardInput extends InputAdapter {

    private final Board board;

    private OrthographicCamera camera;

    private Piece draggingPiece = null;
    private float dragX, dragY;

    public BoardInput(OrthographicCamera camera, Board board) {
        this.camera = camera;

        this.board = board;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;
        Vector3 mouse = new Vector3(screenX, screenY, 0);
        camera.unproject(mouse);
        int row = (int) mouse.y / board.getTileSize();
        int col = (int) mouse.x / board.getTileSize();
        System.out.println("Clicked");

        Tile tile = board.getTiles().get(col).get(row);
        System.out.println("Col clicked: "+ col + " Row clicked: " + row);
        System.out.println("Tile X: " + tile.getXord() + " Tile Y: " + tile.getYord());
        System.out.println(tile.getPiece() == null?"It contains no piece":"At this instance it contains piece: " + tile.getPiece().getName());

        if(tile.getPiece() != null){
            board.setSelectedPiece(tile.getPiece());
            System.out.println("Piece " + tile.getPiece().getName() + " has been selected from col: "+ tile.getPiece().getCol() + " row: " + tile.getPiece().getRow());
            board.generatePieceMoves();
        }

        // Store drag pixel offsets for smooth dragging
        dragX = mouse.x - board.getTileSize()/2f;
        dragY = mouse.y - board.getTileSize()/2f;

        return true;


    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(board.getSelectedPiece() != null){
            Vector3 worldCoords = new Vector3(screenX, screenY, 0);
            camera.unproject(worldCoords);

            // smooth follow with offset
            this.dragX = (worldCoords.x - board.getTileSize()/2f);
            this.dragY = (worldCoords.y - board.getTileSize()/2f);

        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(board.getSelectedPiece() != null){

            Vector3 mouse = new Vector3(screenX, screenY, 0);
            camera.unproject(mouse);

            int row = (int) mouse.y / board.getTileSize();
            int col = (int) mouse.x / board.getTileSize();
            Tile tile = board.getTiles().get(col).get(row);
            System.out.println("Col unClicked: "+ col + " Row unClicked: " + row);
            System.out.println("Tile X: " + tile.getXord() + " Tile Y: " + tile.getYord());
            System.out.println(tile.getPiece() == null?"It contains no piece":"At this instance it contains piece: " + tile.getPiece().getName());

            Move attemptedMove = new Move(board.getSelectedPiece(), (int) mouse.x, (int) mouse.y, board);
            Boolean executed = false;
            for(Move move: board.getValidMoves()){
                System.out.println("Move new X: "+move.getNewX() + " Move new Y: " + move.getNewY());
            }
            for(Move move: board.getValidMoves()){
                if(move.getNewX() == attemptedMove.getNewX() && move.getNewY() == attemptedMove.getNewY() && move.getMovingPiece() == attemptedMove.getMovingPiece()){
                    System.out.println("Action executed");
                    move.execute();
                    executed = true;
                    board.getBotPlayer().takeTurn();
                    break;
                }
            }
            if(!executed){
                board.getSelectedPiece().setCol(board.getSelectedPiece().getCol());
                board.getSelectedPiece().setRow(board.getSelectedPiece().getRow());
                System.out.println("Invalid action");
            }
            board.setSelectedPiece(null);
            board.resetValidMoves();

        }
        return true;
    }

    public Piece getDraggingPiece() {
        return draggingPiece;
    }

    public float getDragX() { return dragX; }
    public float getDragY() { return dragY; }
}

