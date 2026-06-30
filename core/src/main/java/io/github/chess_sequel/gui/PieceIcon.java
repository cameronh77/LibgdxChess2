package io.github.chess_sequel.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.chess_sequel.engine.GameState;
import io.github.chess_sequel.engine.pieces.Piece;
import io.github.chess_sequel.gui.gameScreen.GameScreen;

/**
 * Draggable UI image for a piece in the player's inventory panel. On drag-start it selects
 * the piece on the board and generates its layout moves; on drop it delegates to the
 * {@link BoardActor}'s DragAndDrop target which executes the corresponding
 * {@link io.github.chess_sequel.engine.moves.AlterLayoutMove}.
 */
public class PieceIcon extends Image {

    private final Piece piece;

    public PieceIcon(Piece piece, GameScreen gameScreen) {
        super(new TextureRegionDrawable(
            new TextureRegion(new Texture(piece.getFilePath()))
        ));
        this.piece = piece;
        setSize(gameScreen.getBoardActor().getGameBoard().TILE_SIZE, gameScreen.getBoardActor().getGameBoard().TILE_SIZE);

        DragAndDrop dnd = gameScreen.getDragAndDrop();

        dnd.addSource(new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(
                InputEvent event,
                float x,
                float y,
                int pointer
            ) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(piece);
                Image dragImage = new Image(getDrawable());
                dragImage.setSize(getWidth(), getHeight());
                payload.setDragActor(dragImage);


                gameScreen.getBoardActor().getGameBoard().getGame().getCurrentBoard().setSelectedPiece(piece);
                gameScreen.getBoardActor().getGameBoard().getGame().getCurrentBoard().generatePieceMoves();

                dnd.setDragActorPosition(getActor().getWidth()/2, -getActor().getHeight()/2);

                getActor().setVisible(false);
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target){
                if(target == null){
                    System.out.println("not in board actor");
                    //gameScreen.getInventoryTable().add(PieceIcon.this);
                }
                getActor().setVisible(true);
                gameScreen.getBoardActor().getGameBoard().getGame().getCurrentBoard().setSelectedPiece(null);
            }
        });
    }

    public Piece getPiece() {
        return piece;
    }
}
