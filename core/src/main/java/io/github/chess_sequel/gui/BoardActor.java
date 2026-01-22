package io.github.chess_sequel.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.moves.AlterLayoutMove;
import io.github.chess_sequel.engine.moves.Move;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.gui.GameBoard;
import io.github.chess_sequel.gui.BoardInput;
import io.github.chess_sequel.gui.TextureCache;

public class BoardActor extends Actor {

    private final GameBoard board;
    private final BoardInput input;

    public BoardActor(GameBoard board, BoardInput input, DragAndDrop dragAndDrop) {
        this.board = board;
        this.input = input;

        setSize(board.getPixelWidth(), board.getPixelHeight());

        dragAndDrop.addTarget(new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float vl, int i){
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float vl, int i){
                boolean executed = false;
                AlterLayoutMove attempt = new AlterLayoutMove((Piece) payload.getObject(), (int) v/ board.TILE_SIZE, (int) vl/ board.TILE_SIZE, board.getGame().getCurrentBoard());
                for(Move move: board.getGame().getCurrentBoard().getValidMoves()) {
                    if (move.getNewX() == attempt.getNewX() && move.getNewY() == attempt.getNewY() && move.getMovingPiece() == attempt.getMovingPiece()) {
                        System.out.println("Action executed");
                        move.execute();
                        board.getGame().getPlayer().getPieceInventory().remove((Piece) payload.getObject());
                        board.getGame().getPlayer().getPieces().add((Piece) payload.getObject());
                        board.getGame().setGameState(GameState.CHANGING_INVENTORY);
                        executed = true;
                        break;
                    }
                }
                if(!executed){
                    System.out.println("test no executed");
                }

            }
        });
    }


    @Override
    public void draw(Batch batch, float parentAlpha){
       board.render(batch, getX(), getY(), input);
    }

    public GameBoard getGameBoard(){
        return board;
    }


}

