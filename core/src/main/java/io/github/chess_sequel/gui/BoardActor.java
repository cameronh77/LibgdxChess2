package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Group;
import io.github.chess_sequel.gui.GameBoard;
import io.github.chess_sequel.gui.BoardInput;
import io.github.chess_sequel.gui.TextureCache;

public class BoardActor extends Actor {

    private final GameBoard board;
    private final BoardInput input;

    public BoardActor(GameBoard board, BoardInput input) {
        this.board = board;
        this.input = input;

        setSize(board.getPixelWidth(), board.getPixelHeight());


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Save current batch state
        batch.flush();

        // Apply actor's transformation
        batch.setTransformMatrix(batch.getTransformMatrix()
            .translate(getX(), getY(), 0));

        // Apply actor's color (alpha, tint)
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        // Draw board at (0, 0) relative to actor
        board.render((SpriteBatch) batch, input);

        // Restore batch state
        batch.flush();
        batch.setTransformMatrix(batch.getTransformMatrix().idt());
        batch.setColor(1, 1, 1, 1);
    }
}

