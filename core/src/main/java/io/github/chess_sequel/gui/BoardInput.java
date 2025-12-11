package io.github.chess_sequel.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import io.github.chess_sequel.engine.Game;
import io.github.chess_sequel.engine.location.board.Board;
import io.github.chess_sequel.engine.location.board.MapBoard;
import io.github.chess_sequel.engine.location.board.MatchBoard;
import io.github.chess_sequel.engine.location.Tile;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;

public class BoardInput extends InputAdapter {

    private final GameBoard board;
    private Game game;

    private OrthographicCamera camera;

    private Piece draggingPiece = null;
    private float dragX, dragY;

    public BoardInput(OrthographicCamera camera, GameBoard board, Game game) {
        this.camera = camera;
        this.game = game;
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

        Tile tile = game.getCurrentBoard().getTiles().get(col).get(row);
        System.out.println("Col clicked: "+ col + " Row clicked: " + row);
        System.out.println("Tile X: " + tile.getXord() + " Tile Y: " + tile.getYord());
        System.out.println(tile.getPiece() == null?"It contains no piece":"At this instance it contains piece: " + tile.getPiece().getName());

        if(tile.getPiece() != null){
            game.getCurrentBoard().setSelectedPiece(tile.getPiece());
            System.out.println("Piece " + tile.getPiece().getName() + " has been selected from col: "+ tile.getPiece().getCol() + " row: " + tile.getPiece().getRow());
            game.getCurrentBoard().generatePieceMoves();
        }

        // Store drag pixel offsets for smooth dragging
        dragX = mouse.x - board.TILE_SIZE/2f;
        dragY = mouse.y - board.TILE_SIZE/2f;

        return true;


    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(game.getCurrentBoard().getSelectedPiece() != null){
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

        if(game.getCurrentBoard().getSelectedPiece() != null){

            Vector3 mouse = new Vector3(screenX, screenY, 0);
            camera.unproject(mouse);

            int row = (int) mouse.y / board.TILE_SIZE;
            int col = (int) mouse.x / board.TILE_SIZE;
            Tile tile = game.getCurrentBoard().getTiles().get(col).get(row);
            System.out.println("Col unClicked: "+ col + " Row unClicked: " + row);
            System.out.println("Tile X: " + tile.getXord() + " Tile Y: " + tile.getYord());
            System.out.println(tile.getPiece() == null?"It contains no piece":"At this instance it contains piece: " + tile.getPiece().getName());

            Move attemptedMove = new Move(game.getCurrentBoard().getSelectedPiece(), (int) mouse.x, (int) mouse.y, board.game.getCurrentBoard());
            Boolean executed = false;
            for(Move move: game.getCurrentBoard().getValidMoves()){
                System.out.println("Move new X: "+move.getNewX() + " Move new Y: " + move.getNewY());
            }
            for(Move move: game.getCurrentBoard().getValidMoves()){
                if(move.getNewX() == attemptedMove.getNewX() && move.getNewY() == attemptedMove.getNewY() && move.getMovingPiece() == attemptedMove.getMovingPiece()){
                    System.out.println("Action executed");
                    move.execute();
                    if(game.getCurrentBoard() instanceof MatchBoard){
                        game.getCurrentBoard().getBotPlayer().takeTurn(game.getCurrentBoard());
                    }
                    if(game.getCurrentBoard() instanceof MapBoard){
                        if(game.getCurrentBoard().getTiles().get(move.getNewX()).get(move.getNewY()).getInteractable() != null){
                            game.getCurrentBoard().getTiles().get(move.getNewX()).get(move.getNewY()).getInteractable().interaction();
                        }
                    }
                    executed = true;

                    break;
                }
            }
            if(!executed){
                game.getCurrentBoard().getSelectedPiece().setCol(game.getCurrentBoard().getSelectedPiece().getCol());
                game.getCurrentBoard().getSelectedPiece().setRow(game.getCurrentBoard().getSelectedPiece().getRow());
                System.out.println("Invalid action");
            }
            game.getCurrentBoard().setSelectedPiece(null);
            game.getCurrentBoard().resetValidMoves();

        }
        return true;
    }

    public Piece getDraggingPiece() {
        return draggingPiece;
    }

    public float getDragX() { return dragX; }
    public float getDragY() { return dragY; }
}

