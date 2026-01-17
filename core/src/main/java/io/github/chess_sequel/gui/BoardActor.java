package io.github.chess_sequel.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
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
    public void draw(Batch batch, float parentAlpha){
       board.render(batch, getX(), getY(), input);
    }

    public GameBoard getGameBoard(){
        return board;
    }


}

